package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.TargetService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 最高分最低分统计（不包含题目，题目在 mapreduce 中统计）
 *
 * @author yiding_he
 */
@AggrTaskMeta(taskType = "score_minmax")
@Component
public class MinMaxTask extends AggrTask {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Override
    public void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Target target = taskInfo.getTarget();
        Range range = taskInfo.getRange();
        String subjectId = targetService.getTargetSubjectId(projectId, target);

        // 查询考生列表
        List<String> studentIds = studentService.getStudentIds(projectId, subjectId, range);

        if (studentIds.isEmpty()) {
            saveMinMax(projectId, target, range, Value.of(0d), Value.of(0d));

        } else {
            // 查询每个考生的分数，得出最高分最低分
            Value<Double> min = Value.of((double) Integer.MAX_VALUE), max = Value.of(0d);
            queryMinMax(projectId, target, studentIds, min, max);

            // 保存最高分最低分
            saveMinMax(projectId, target, range, min, max);
        }
    }

    private void queryMinMax(
            String projectId, Target target, List<String> studentIds, Value<Double> min, Value<Double> max) {

        for (String studentId : studentIds) {
            double totalScore = scoreService.getScore(projectId, new Range(Range.STUDENT, studentId), target);

            if (totalScore < min.get()) {
                min.set(totalScore);
            } else if (totalScore > max.get()) {
                max.set(totalScore);
            }
        }
    }

    private void saveMinMax(String projectId, Target target, Range range, Value<Double> min, Value<Double> max) {
        Document id = Mongo.query(projectId, range, target);
        scoreDatabase.getCollection("score_minmax")
                .updateOne(id, $set(doc("min", min.get()).append("max", max.get())), UPSERT);
    }

}