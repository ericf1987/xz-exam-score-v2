package com.xz.examscore.scanner;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.NumberUtil;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.score.ScorePattern;
import com.xz.examscore.services.ImportProjectService;
import com.xz.examscore.services.QuestService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.SubjectService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$in;
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
    MongoClient scannerMongoClient2;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    QuestService questService;

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    SubjectService subjectService;

    public MongoClient getMongoClient(String project) {
        Document projectDoc = scannerMongoClient.getDatabase("project_database")
                .getCollection("project").find(doc("projectId", project)).first();
        if (null == projectDoc) {
            return scannerMongoClient2;
        }
        return scannerMongoClient;
    }

    public Document findProject(String project) {
        return getMongoClient(project).getDatabase("project_database")
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
        MongoCollection<Document> collection = getMongoClient(project).getDatabase(dbName).getCollection("students");
        AtomicInteger counter = new AtomicInteger();
        collection.find(doc()).forEach(
                (Consumer<Document>) doc -> importStudentScore(project, subjectId, doc, counter));

        LOG.info("已导入 " + counter.get() + " 名学生...");
    }

    /**
     * @param projectId 项目ID
     * @param subjectId 网阅数据库科目ID
     * @param document  网阅数据库学生信息
     * @param counter   计数器
     */
    public void importStudentScore(String projectId, String subjectId, Document document, AtomicInteger counter) {
        String studentId = document.getString("studentId");
        Document student = studentService.findStudent(projectId, studentId);

        if (student == null) {
            throw new IllegalStateException("找不到项目 " + projectId + " 的考生 " + studentId);
        }

        List<String> subjectList = importProjectService.separateSubject(subjectId);
        scoreDatabase.getCollection("score").deleteMany(
                doc("project", projectId).append("student", studentId).append("subject", $in(subjectList))
        );

        saveObjectiveScores(projectId, subjectId, document, student);
        saveSubjectiveScores(projectId, subjectId, document, student);

        if (counter.incrementAndGet() % 100 == 0) {
            LOG.info("已导入科目 " + subjectId + " 的 " + counter.get() + " 名学生...");
        }
    }


    private void fixMissingSubjectQuest(List<Document> subQuestList, List<Document> subjectiveList) {
        //网阅题目ID列表
        List<String> subjectiveIds = subjectiveList.stream().map(subQuestItem -> subQuestItem.getString("questionNo")).collect(Collectors.toList());

        //统计库题目ID列表
        List<String> subQuestIds = subQuestList.stream().map(subQuestItem -> subQuestItem.getString("questNo")).collect(Collectors.toList());

        for (int i = 0; i < subQuestIds.size(); i++) {
            //判断是否网阅题目ID中存在遗漏
            if (!subjectiveIds.contains(subQuestIds.get(i))) {
                Document subQuest = subQuestList.get(i);
                subjectiveList.add(
                        doc("questionNo", subQuest.getString("questNo"))
                                .append("score", 0)
                                .append("fullScore", subQuest.getDouble("score"))
                                .append("missing", true)
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void saveSubjectiveScores(String projectId, String subjectId, Document document, Document student) {
        List<Document> subjectiveList = (List<Document>) document.get("subjectiveList");

        //获取统计集合中主观题信息
        List<Document> subQuestList = new ArrayList<>();

        //获取拆分后所有综合科目的题目列表（quest_list）
        List<String> subjectIds = importProjectService.separateSubject(subjectId);
        subjectIds.forEach(s -> {
            List<Document> subList = questService.getQuests(projectId, s, false);
            subQuestList.addAll(subList);
        });

        //对于统计集合中有的，但是网阅数据中没有的数据，则插入一条记录，并标识missing=true
        fixMissingSubjectQuest(subQuestList, subjectiveList);

        for (Document subjectiveItem : subjectiveList) {
            String questionNo = subjectiveItem.getString("questionNo");
            double score = Double.parseDouble(subjectiveItem.get("score").toString());
            double fullScore = Double.parseDouble(subjectiveItem.get("fullScore").toString());
            Boolean missing = subjectiveItem.getBoolean("missing");
            //主观题学生作答的切图所在的URL
            Map<String, Object> url = (Map<String, Object>) subjectiveItem.get("url");
            String sid = getSubjectIdInQuestList(projectId, questionNo, subjectId);
            Document scoreDoc = doc("project", projectId)
                    .append("subject", sid)
                    .append("questNo", questionNo)
                    .append("score", score)
                    .append("right", NumberUtil.equals(score, fullScore))
                    .append("isObjective", false)
                    .append("student", student.getString("student"))
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("area", student.getString("area"))
                    .append("city", student.getString("city"))
                    .append("province", student.getString("province"))
                    .append("url", url)
                    .append("md5", MD5.digest(UUID.randomUUID().toString()));
            if (null != missing && missing) {
                scoreDoc.append("missing", true);
            }

            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    @SuppressWarnings("unchecked")
    private void saveObjectiveScores(String projectId, String subjectId, Document document, Document student) {
        List<Document> objectiveList = (List<Document>) document.get("objectiveList");

        for (Document objectiveItem : objectiveList) {

            String questionNo = objectiveItem.getString("questionNo");
            String sid = getSubjectIdInQuestList(projectId, questionNo, subjectId);
            Document quest = questService.findQuest(projectId, sid, questionNo);
            if (null == quest) {
                LOG.error("题目为空：projectId={}, subjectId={}, sid={}, questionNo={}", projectId, subjectId, sid, questionNo);
                throw new IllegalArgumentException("获取题目信息失败！统计失败");
            }
            double fullScore = getFullScore(quest, objectiveItem);
            String studentAnswer = objectiveItem.getString("answerContent").toUpperCase();
            //标准答案数据从统计数据库的quest_list中获取
            //String standardAnswer = objectiveItem.getString("standardAnswer").toUpperCase();
            String standardAnswer = getStdAnswerFromQuest(objectiveItem, quest);

            if (StringUtil.isBlank(studentAnswer)) {
                throw new IllegalStateException("客观题没有考生作答, project=" +
                        projectId + ", subject=" + sid + ", quest=" + objectiveItem);
            }

            if (StringUtil.isBlank(standardAnswer)) {
                throw new IllegalStateException("客观题没有标准答案, project=" +
                        projectId + ", subject=" + sid + ", quest=" + objectiveItem);
            }

            Boolean awardScoreTag = quest.getBoolean("awardScoreTag");

            ScoreAndRight scoreAndRight = calculateScore(fullScore, standardAnswer, studentAnswer, awardScoreTag);

            Document scoreDoc = doc("project", projectId)
                    .append("subject", sid)
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
                    .append("province", student.getString("province"))
                    .append("md5", MD5.digest(UUID.randomUUID().toString()));

            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    public String getSubjectIdInQuestList(String projectId, String questionNo, String subjectId) {
        //如果科目需要拆分
        if (subjectId.length() != ImportProjectService.SUBJECT_LENGTH) {
            Document q1 = questService.findQuest(projectId, subjectId, questionNo);
            if (null != q1) {
                return q1.getString("subject");
            } else {
                List<String> subjectIds = importProjectService.separateSubject(subjectId);
                Document q2 = questService.findQuest(projectId, subjectIds, questionNo);
                return q2.getString("subject");
            }
        } else {
            return subjectId;
        }
    }

    //获取标准答案
    private String getStdAnswerFromQuest(Document objectiveItem, Document quest) {
        String standardAnswer = objectiveItem.getString("standardAnswer").toUpperCase();

        Boolean isObjective = quest.getBoolean("isObjective");
        if (isObjective != null && isObjective) {
            if (!StringUtils.isEmpty(quest.getString("scoreRule"))) {
                standardAnswer = quest.getString("scoreRule");
            } else {
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

    /**
     * 计算分数
     *
     * @param fullScore      题目满分值
     * @param standardAnswer 题目的标准答案
     * @param answerContent  考生作答
     * @param awardScoreTag  是否为给分题（一律给满分/一律不给分）：true=给满分，false=不给分，null=按照规则给分
     * @return 分数
     */
    protected static ScoreAndRight calculateScore(
            double fullScore, String standardAnswer, String answerContent, Boolean awardScoreTag) {

        //如果给分标记为true，则直接给分
        if (null != awardScoreTag && awardScoreTag) {
            return new ScoreAndRight(fullScore, true);
        }
        //其他情况则根据给分规则来判断
        else {
            if (answerContent.equals(standardAnswer)) {
                return new ScoreAndRight(fullScore, true);
            } else {
                ScorePattern scorePattern = new ScorePattern(standardAnswer, fullScore);
                double score = scorePattern.getScore(answerContent);
                return new ScoreAndRight(score, score > 0);
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
