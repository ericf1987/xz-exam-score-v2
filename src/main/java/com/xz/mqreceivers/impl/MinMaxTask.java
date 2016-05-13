package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import com.xz.services.TargetService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 最高分最低分统计（不包含题目，题目在 mapreduce 中统计）
 *
 * @author yiding_he
 */
@ReceiverInfo(taskType = "minmax")
@Component
public class MinMaxTask extends Receiver {

    static final Logger LOG = LoggerFactory.getLogger(MinMaxTask.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Override
    public void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Target target = aggrTask.getTarget();
        String subjectId = getSubjectId(target);
        Range range = aggrTask.getRange();

        Value<Double> min = Value.of((double) Integer.MAX_VALUE), max = Value.of(0d);
        List<String> studentIds = studentService.getStudentList(projectId, subjectId, range);

        if (studentIds.isEmpty()) {
            LOG.info("学生数量为0, projectId={}, subjectId={}, range={}", projectId, subjectId, range);
        } else {
            LOG.info("找到{}个学生，计算最大最小分数{}", studentIds.size(), target);
        }

        queryMinMax(projectId, target, studentIds, min, max);
        saveMinMax(projectId, target, range, min, max);
    }

    private void saveMinMax(String projectId, Target target, Range range, Value<Double> min, Value<Double> max) {
        Document id = Mongo.generateId(projectId, range, target);
        Document result = new Document("_id", id).append("value",
                new Document("min", min.get()).append("max", max.get()));

        scoreDatabase.getCollection("min_max_score")
                .replaceOne(new Document("_id", id), result, new UpdateOptions().upsert(true));
    }

    private void queryMinMax(String projectId, Target target, List<String> studentIds, Value<Double> min, Value<Double> max) {

        for (String studentId : studentIds) {
            double totalScore = scoreService.getTotalScore(projectId, new Range(Range.STUDENT, studentId), target);

            if (totalScore < min.get()) {
                min.set(totalScore);
            } else if (totalScore > max.get()) {
                max.set(totalScore);
            }
        }
    }

    // 取任务信息中的科目ID
    private String getSubjectId(Target target) {
        return targetService.getTargetSubjectId(target);
    }
}
