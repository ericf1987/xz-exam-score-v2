package com.xz.scanner;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.NumberUtil;
import com.xz.services.QuestService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private void importStudentScore(String projectId, String subjectId, Document document, AtomicInteger counter) {
        String studentId = document.getString("studentId");
        Document student = studentService.findStudent(projectId, studentId);

        scoreDatabase.getCollection("score").deleteMany(
                doc("project", projectId).append("student", studentId)
        );

        if (student == null) {
            throw new IllegalStateException("找不到项目 " + projectId + " 的考生 " + studentId);
        }

        saveObjectiveScores(projectId, subjectId, document, student);
        saveSubjectiveScores(projectId, subjectId, document, student);

        if (counter.incrementAndGet() % 100 == 0) {
            LOG.info("已导入 " + counter.get() + " 名学生...");
        }
    }

    @SuppressWarnings("unchecked")
    private void saveSubjectiveScores(String projectId, String subjectId, Document document, Document student) {
        List<Document> subjectiveList = (List<Document>) document.get("subjectiveList");

        for (Document subjectiveItem : subjectiveList) {
            String questionNo = subjectiveItem.getString("questionNo");
            double score = subjectiveItem.getDouble("score");
            double fullScore = subjectiveItem.getDouble("fullScore");

            Document scoreDoc = doc("project", projectId)
                    .append("subject", subjectId)
                    .append("questNo", questionNo)
                    .append("score", score)
                    .append("right", NumberUtil.equals(score, fullScore))
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

    @SuppressWarnings("unchecked")
    private void saveObjectiveScores(String projectId, String subjectId, Document document, Document student) {
        List<Document> objectiveList = (List<Document>) document.get("objectiveList");

        for (Document objectiveItem : objectiveList) {

            String questionNo = objectiveItem.getString("questionNo");
            Document quest = questService.findQuest(projectId, subjectId, questionNo);
            double fullScore = getFullScore(quest, objectiveItem);

            ScoreAndRight scoreAndRight = calculateScore(
                    fullScore,
                    objectiveItem.getString("standardAnswer"),
                    objectiveItem.getString("answerContent")
            );

            Document scoreDoc = doc("project", projectId)
                    .append("subject", subjectId)
                    .append("questNo", questionNo)
                    .append("score", scoreAndRight.score)
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

    //////////////////////////////////////////////////////////////

    private double getFullScore(Document quest, Document objectiveItem) {
        if (quest == null) {
            return Double.parseDouble(objectiveItem.getString("fullScore"));
        } else {
            return quest.getDouble("score");
        }
    }

    private ScoreAndRight calculateScore(double fullScore, String standardAnswer, String answerContent) {
        if (answerContent.equals(standardAnswer)) {
            return new ScoreAndRight(fullScore, true);
        } else {
            return new ScoreAndRight(0, false);
        }
    }

    //////////////////////////////////////////////////////////////

    private static class ScoreAndRight {

        public double score;

        public boolean right;

        public ScoreAndRight(double score, boolean right) {
            this.score = score;
            this.right = right;
        }
    }
}
