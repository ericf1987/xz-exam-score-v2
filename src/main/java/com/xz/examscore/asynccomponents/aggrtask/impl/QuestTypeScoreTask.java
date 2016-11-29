package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

@Component
@AggrTaskMeta(taskType = "quest_type_score")
public class QuestTypeScoreTask extends AggrTask {

    static final Logger LOG = LoggerFactory.getLogger(QuestTypeScoreTask.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    QuestService questService;

    @Autowired
    StudentService studentService;

    @Autowired
    SubjectService subjectService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        String studentId = taskInfo.getRange().getId();

        List<String> subjectIds = subjectService.querySubjects(projectId);
        //根据科目分多线程处理题型得分
        for(String subjectId : subjectIds){
            runQuestTypeScoreDistributor(projectId, studentId, subjectId);
        }
    }

    private void runQuestTypeScoreDistributor(String projectId, String studentId, String subjectId) {
        QuestTypeScoreTaskDistributor distributor = new QuestTypeScoreTaskDistributor(projectId, studentId, subjectId);
        distributor.start();
    }

    private class QuestTypeScoreTaskDistributor extends Thread{
        private String projectId;

        private String subjectId;

        private String studentId;

        private Map<String, Double> questTypeScoresMap = new HashMap<>();

        public QuestTypeScoreTaskDistributor(String projectId, String subjectId, String studentId){
            this.projectId = projectId;
            this.subjectId = subjectId;
            this.studentId = studentId;
        }

        public Map<String, Double> getQuestTypeScoresMap() {
            return questTypeScoresMap;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        @Override
        public void run() {
            LOG.info("线程{}开始执行，项目{}，科目{}, 学生{}的试卷题型得分统计...", this.getName(), projectId, subjectId, studentId);
            doQuestTypeScoreTaskDistribute(projectId, subjectId, studentId, questTypeScoresMap);
        }
    }

    private void doQuestTypeScoreTaskDistribute(String projectId, String subjectId, String studentId, Map<String, Double> questTypeScoresMap) {
        Document studentDoc = studentService.findStudent(projectId, studentId);
        FindIterable<Document> scores = scoreService.getStudentSubjectScores(projectId, studentId, subjectId);

        // 对题型进行归类，分数累加
        for (Document score : scores) {
            String questNo = score.getString("questNo");
            double scoreValue = score.getDouble("score");

            Document quest = questService.findQuest(projectId, subjectId, questNo);
            String questTypeId = quest.getString("questionTypeId");

            // 有的科目可能因为没有录入题目而导致该属性为空。
            if (questTypeId == null) {
                continue;
            }

            if (!questTypeScoresMap.containsKey(questTypeId)) {
                questTypeScoresMap.put(questTypeId, scoreValue);
            } else {
                questTypeScoresMap.put(questTypeId, questTypeScoresMap.get(questTypeId) + scoreValue);
            }
        }

        // 保存考生题型得分
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_type_score");

        for (String questTypeId : questTypeScoresMap.keySet()) {
            Document query = doc("project", projectId).append("student", studentId).append("questType", questTypeId);
            double score = questTypeScoresMap.get(questTypeId);
            double fullScore = fullScoreService.getFullScore(projectId, Target.questType(questTypeId));
            double rate = score / fullScore;

            Document update = doc("score", score)
                    .append("rate", rate)
                    .append("class", studentDoc.getString("class"))
                    .append("school", studentDoc.getString("school"))
                    .append("area", studentDoc.getString("area"))
                    .append("city", studentDoc.getString("city"))
                    .append("province", studentDoc.getString("province"));

            UpdateResult result = collection.updateMany(query, $set(update));
            if (result.getMatchedCount() == 0) {
                collection.insertOne(
                        query.append("score", score)
                                .append("rate", rate)
                                .append("class", studentDoc.getString("class"))
                                .append("school", studentDoc.getString("school"))
                                .append("area", studentDoc.getString("area"))
                                .append("city", studentDoc.getString("city"))
                                .append("province", studentDoc.getString("province"))
                                .append("md5", MD5.digest(UUID.randomUUID().toString()))
                );
            }
        }
    }
}
