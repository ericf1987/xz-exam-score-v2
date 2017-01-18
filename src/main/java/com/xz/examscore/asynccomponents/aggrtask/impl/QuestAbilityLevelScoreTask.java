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
import org.apache.commons.lang.BooleanUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2017/1/4.
 */
@Component
@AggrTaskMeta(taskType = "quest_ability_level_score")
public class QuestAbilityLevelScoreTask extends AggrTask {


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

    @Autowired
    TargetService targetService;

    @Autowired
    QuestAbilityLevelService questAbilityLevelService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        String studentId = taskInfo.getRange().getId();
        Document studentDoc = studentService.findStudent(projectId, studentId);

        List<String> subjectIds = subjectService.querySubjects(projectId);
        Map<String, Double> questAbilityLevelMap = new HashMap<>();

        List<QuestAbilityLevelTaskDistributor> distributors = countQuestAbilityLevel(projectId, studentId, subjectIds);

        for (QuestAbilityLevelTaskDistributor distributor : distributors) {
            try {
                distributor.join();
                questAbilityLevelMap.putAll(distributor.getQuestAbilityLevelMap());
            } catch (InterruptedException e) {
                LOG.error("等待题目能力层级得分处理线程结束失败,考试ID：{}，项目ID：{}, 学生ID：{}", distributor.getProjectId(), distributor.getSubjectId(), distributor.getStudentId());
                return;
            }
        }

        // 保存考生题型得分
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_ability_level_score");

        for (String questAbilityLevel : questAbilityLevelMap.keySet()) {
            Document query = doc("project", projectId).append("student", studentId).append("questAbilityLevel", questAbilityLevel);
            double score = questAbilityLevelMap.get(questAbilityLevel);
            double fullScore = fullScoreService.getFullScore(projectId, Target.questAbilityLevel(questAbilityLevel));
            double rate = score / fullScore;

            String subjectId = questAbilityLevelService.getSubjectId(questAbilityLevel);
            String levelOrAbility = questAbilityLevelService.getLevel(questAbilityLevel);

            Document update = doc("score", score)
                    .append("rate", rate)
                    .append("subject", subjectId)
                    .append("levelOrAbility", levelOrAbility)
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
                                .append("subject", subjectId)
                                .append("levelOrAbility", levelOrAbility)
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

    private List<QuestAbilityLevelTaskDistributor> countQuestAbilityLevel(String projectId, String studentId, List<String> subjectIds) {
        //根据科目分多线程处理题型得分
        List<QuestAbilityLevelTaskDistributor> distributors = new ArrayList<>();
        for (String subjectId : subjectIds) {
            QuestAbilityLevelTaskDistributor distributor = runQuestAbilityLevelDistributor(projectId, studentId, subjectId);
            distributors.add(distributor);
        }
        return distributors;
    }

    private QuestAbilityLevelTaskDistributor runQuestAbilityLevelDistributor(String projectId, String studentId, String subjectId) {
        QuestAbilityLevelTaskDistributor distributor = new QuestAbilityLevelTaskDistributor(projectId, subjectId, studentId);
        distributor.start();
        return distributor;
    }

    private class QuestAbilityLevelTaskDistributor extends Thread {
        private String projectId;

        private String subjectId;

        private String studentId;

        private Map<String, Double> questAbilityLevelMap = new HashMap<>();

        public QuestAbilityLevelTaskDistributor(String projectId, String subjectId, String studentId) {
            this.projectId = projectId;
            this.subjectId = subjectId;
            this.studentId = studentId;
        }

        public Map<String, Double> getQuestAbilityLevelMap() {
            return questAbilityLevelMap;
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
            //LOG.info("线程{}开始执行，项目{}，科目{}, 学生{}的题型能力层级得分统计...", this.getName(), projectId, subjectId, studentId);
            doQuestAbilityLevelTaskDistribute(projectId, subjectId, studentId, questAbilityLevelMap);
        }
    }

    public void doQuestAbilityLevelTaskDistribute(String projectId, String subjectId, String studentId, Map<String, Double> questTypeScoresMap) {

        FindIterable<Document> scores = scoreService.getStudentSubjectScores(projectId, studentId, subjectId);

        // 对题型进行归类，分数累加
        for (Document score : scores) {
            String questNo = score.getString("questNo");
            double scoreValue = score.getDouble("score");

            Document quest = questService.findQuest(projectId, subjectId, questNo);
            String questAbilityLevel = quest.getString("questAbilityLevel");

            // 有的科目可能因为没有录入题目而导致该属性为空。
            if (questAbilityLevel == null) {
                continue;
            }

            if (!questTypeScoresMap.containsKey(questAbilityLevel)) {
                questTypeScoresMap.put(questAbilityLevel, scoreValue);
            } else {
                questTypeScoresMap.put(questAbilityLevel, questTypeScoresMap.get(questAbilityLevel) + scoreValue);
            }
        }
    }

}
