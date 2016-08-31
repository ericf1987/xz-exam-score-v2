package com.xz.examscore.scanner;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.NumberUtil;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.score.ScorePattern;
import com.xz.examscore.services.QuestService;
import com.xz.examscore.services.StudentService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/06/16
 *
 * @author yiding_he
 */
@Service
public class ScannerDBService {

    static final Logger LOG = LoggerFactory.getLogger(ScannerDBService.class);

    @Autowired
    MongoClient scannerMongoClient;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    QuestService questService;

    public Document findProject(String project) {
        return scannerMongoClient.getDatabase("project_database")
                .getCollection("project").find(doc("projectId", project)).first();
    }

    /**
     * 从网阅数据库中导入成绩
     *
     * @param project 项目ID
     */
    public void importProjectScore(String project) {

        Document projectDoc = findProject(project);
        if (projectDoc == null) {
            LOG.error("没有找到项目" + project);
            return;
        }

        Document subjectCodes = (Document) projectDoc.get("subjectcodes");
        List<String> subjectIds = new ArrayList<>(subjectCodes.keySet());

        for (String subjectId : subjectIds) {
            importSubjectScore(project, subjectId);
        }
    }

    /**
     * 从网阅数据库中导入成绩
     *
     * @param project   项目ID
     * @param subjectId 科目ID
     */
    public void importSubjectScore(String project, String subjectId) {
        String dbName = project + "_" + subjectId;
        LOG.info("导入 " + dbName + " 的成绩...");
        MongoCollection<Document> collection = scannerMongoClient.getDatabase(dbName).getCollection("students");
        AtomicInteger counter = new AtomicInteger();

        collection.find(doc()).forEach(
                (Consumer<Document>) doc -> importStudentScore(project, subjectId, doc, counter));

        LOG.info("已导入 " + counter.get() + " 名学生...");
    }

    public void importStudentScore(String projectId, String subjectId, Document document, AtomicInteger counter) {
        String studentId = document.getString("studentId");
        Document student = studentService.findStudent(projectId, studentId);

        scoreDatabase.getCollection("score").deleteMany(
                doc("project", projectId).append("student", studentId).append("subject", subjectId)
        );

        if (student == null) {
            throw new IllegalStateException("找不到项目 " + projectId + " 的考生 " + studentId);
        }

        saveObjectiveScores(projectId, subjectId, document, student);
        saveSubjectiveScores(projectId, subjectId, document, student);

        if (counter.incrementAndGet() % 100 == 0) {
            LOG.info("已导入科目 " + subjectId + " 的 " + counter.get() + " 名学生...");
        }
    }

    @SuppressWarnings("unchecked")
    private void saveSubjectiveScores(String projectId, String subjectId, Document document, Document student) {
        List<Document> subjectiveList = (List<Document>) document.get("subjectiveList");

        for (Document subjectiveItem : subjectiveList) {
            String questionNo = subjectiveItem.getString("questionNo");
            double score = Double.parseDouble(subjectiveItem.get("score").toString());
            double fullScore = Double.parseDouble(subjectiveItem.get("fullScore").toString());

            Document scoreDoc = doc("project", projectId)
                    .append("subject", subjectId)
                    .append("questNo", questionNo)
                    .append("score", score)
                    .append("right", NumberUtil.equals(score, fullScore))
                    .append("isObjective", false)
                    .append("student", student.getString("student"))
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("area", student.getString("area"))
                    .append("city", student.getString("city"))
                    .append("province", student.getString("province"));

            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    @SuppressWarnings("unchecked")
    private void saveObjectiveScores(String projectId, String subjectId, Document document, Document student) {
        List<Document> objectiveList = (List<Document>) document.get("objectiveList");

        for (Document objectiveItem : objectiveList) {

            String questionNo = objectiveItem.getString("questionNo");
            Document quest = questService.findQuest(projectId, subjectId, questionNo);
            double fullScore = getFullScore(quest, objectiveItem);
            String studentAnswer = objectiveItem.getString("answerContent").toUpperCase();
            //标准答案数据从统计数据库的quest_list中获取
            //String standardAnswer = objectiveItem.getString("standardAnswer").toUpperCase();
            String standardAnswer = getStdAnswerFromQuest(objectiveItem, quest);

            if (StringUtil.isBlank(studentAnswer)) {
                throw new IllegalStateException("客观题没有考生作答, project=" +
                        projectId + ", subject=" + subjectId + ", quest=" + objectiveItem);
            }

            Boolean awardScoreTag = quest.getBoolean("awardScoreTag");

            ScoreAndRight scoreAndRight = calculateScore(fullScore, standardAnswer, studentAnswer, awardScoreTag);

            Document scoreDoc = doc("project", projectId)
                    .append("subject", subjectId)
                    .append("questNo", questionNo)
                    .append("score", scoreAndRight.score)
                    .append("answer", studentAnswer)
                    .append("right", scoreAndRight.right)
                    .append("isObjective", true)
                    .append("student", student.getString("student"))
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("area", student.getString("area"))
                    .append("city", student.getString("city"))
                    .append("province", student.getString("province"));

            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    //获取标准答案
    private String getStdAnswerFromQuest(Document objectiveItem, Document quest) {
        String standardAnswer = objectiveItem.getString("standardAnswer").toUpperCase();

        Boolean isObjective = quest.getBoolean("isObjective");
        if(isObjective != null && isObjective){
            if(!StringUtils.isEmpty(quest.getString("scoreRule"))){
                standardAnswer = quest.getString("scoreRule");
            }else{
                standardAnswer = quest.getString("answer");
            }
        }
        return standardAnswer;
    }

    //////////////////////////////////////////////////////////////

    private double getFullScore(Document quest, Document objectiveItem) {
        if (quest == null) {
            return Double.parseDouble(objectiveItem.getString("fullScore"));
        } else {
            return quest.getDouble("score");
        }
    }

    protected static ScoreAndRight calculateScore(double fullScore, String standardAnswer, String answerContent, Boolean awardScoreTag) {
        //如果给分标记为空，则根据给分规则来判断
        if(null == awardScoreTag){
            if(answerContent.equals(standardAnswer)){
                return new ScoreAndRight(fullScore, true);
            }else{
                ScorePattern scorePattern = new ScorePattern(standardAnswer, fullScore);
                double score = scorePattern.getScore(answerContent);
                return new ScoreAndRight(score, score > 0);
            }
        }
        //如果给分标记为true，则直接给分
        else {
            if (awardScoreTag) {
                return new ScoreAndRight(fullScore, true);
            }
            //如果给分标记为false，则不给分
            else {
                return new ScoreAndRight(0, true);
            }
        }
    }

    private static List<String> parseUsrAnswer(String answerContent) {
        List<String> result = new ArrayList<>();
        for (char c : answerContent.toCharArray()) {
            result.add(new String(new char[]{c}));
        }
        Collections.sort(result);
        return result;
    }

    private static List<String> parseStdAnswer(String standardAnswer) {
        List<String> result = new ArrayList<>(Arrays.asList(standardAnswer.split(",")));
        Collections.sort(result);
        return result;
    }

    //////////////////////////////////////////////////////////////

    public static class ScoreAndRight {

        public double score;

        public boolean right;

        public ScoreAndRight(double score, boolean right) {
            this.score = score;
            this.right = right;
        }
    }
}