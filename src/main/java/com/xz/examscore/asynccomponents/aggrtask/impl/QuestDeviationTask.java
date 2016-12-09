package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2016/5/29.
 */
@SuppressWarnings("unchecked")
@AggrTaskMeta(taskType = "quest_deviation")
@Component
public class QuestDeviationTask extends AggrTask {

    static final Logger LOG = LoggerFactory.getLogger(QuestDeviationTask.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    public static final double DEVIATION_RATE = 0.27d;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();
        String questId = target.getId().toString();
        //1.查询题目所在的科目的学生总分信息

        Target subjectTarget = Target.subject(targetService.getTargetSubjectId(projectId, target));

        MongoCollection<Document> questDeviationCol = scoreDatabase.getCollection("quest_deviation");

        List<Map<String, Object>> students = new ArrayList();
        //获取每个学生的总分
        studentService.getStudentIds(projectId, range, subjectTarget).forEach(studentId -> {
            double totalScore = scoreService.getScore(projectId, Range.student(studentId), subjectTarget);
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("student", studentId);
            studentMap.put("totalScore", totalScore);
            students.add(studentMap);
        });

        if (students.isEmpty()) {
            LOG.warn("找不到学生总得分!: project={}, range={}, target={}", projectId, range.getId(), target.getId().toString());
            return;
        }

        //获取科目排名前27%的学生
        List<String> topStudentIds = getTopOrButton(students, true);
        //获取科目排名后27%的学生
        List<String> buttomStudentIds = getTopOrButton(students, false);

        double subScore = getSubScore(topStudentIds, buttomStudentIds, projectId, questId);

        double questFullScore = fullScoreService.getFullScore(projectId, target);

        if (questFullScore == 0) {
            LOG.warn("题目满分值为0，数据异常！: quest={}", questId);
            return;
        }

        double deviation = DoubleUtils.round(subScore / questFullScore, false);

        UpdateResult result = questDeviationCol.updateMany(
                new Document("project", projectId).
                        append("range", Mongo.range2Doc(range)).
                        append("quest", questId),
                $set(doc("deviation", deviation))
        );
        if (result.getMatchedCount() == 0) {
            questDeviationCol.insertOne(
                    new Document("project", projectId)
                            .append("range", Mongo.range2Doc(range))
                            .append("quest", questId).append("deviation", deviation)
                            .append("deviation", deviation).append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }

    }

    private List<String> getTopOrButton(List<Map<String, Object>> students, boolean isTop) {
        //按得分从高到低排序
        if (isTop) {
            Collections.sort(students, (Map<String, Object> d1, Map<String, Object> d2) ->
                    Double.valueOf(d2.get("totalScore").toString()).compareTo(Double.valueOf(d1.get("totalScore").toString()))
            );
        } else {
            Collections.sort(students, (Map<String, Object> d1, Map<String, Object> d2) ->
                    Double.valueOf(d1.get("totalScore").toString()).compareTo(Double.valueOf(d2.get("totalScore").toString()))
            );
        }
        int deviationCount = (int) Math.ceil(students.size() * DEVIATION_RATE);
        return students.subList(0, deviationCount).stream().map(student -> student.get("student").toString()).collect(Collectors.toList());
    }

    private double getSubScore(List<String> topStudentIds, List<String> buttomStudentIds, String projectId, String questId) {
        return getQuestAver(topStudentIds, projectId, questId) - getQuestAver(buttomStudentIds, projectId, questId);
    }

    //计算题目平均得分
    private double getQuestAver(List<String> studentIds, String projectId, String questId) {
        MongoCollection<Document> scoreCol = scoreDatabase.getCollection("score");
        double sum = 0;
        int count = studentIds.size();
        for (String studentId : studentIds) {
            Document query = doc("project", projectId).append("student", studentId).append("quest", questId);
            Document doc = scoreCol.find(query).first();
            //如果出现有选做题的情况，则会出现空指针异常，在此做好判断
            if(null != doc){
                sum += doc.getDouble("score");
            }
        }
        System.out.println("学生总分：" + sum + ", 学生人数：" + count + ", 平均得分：" + DoubleUtils.round(sum / count, false));
        return count == 0 ? 0 : DoubleUtils.round(sum / count, false);
    }

}
