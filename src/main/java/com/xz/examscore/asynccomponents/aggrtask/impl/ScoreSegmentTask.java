package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.util.Mongo;
import com.xz.examscore.util.ScoreSegmentCounter;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;

@Component
@AggrTaskMeta(taskType = "score_segment")
public class ScoreSegmentTask extends AggrTask {

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();

        List<String> studentIds = studentService.getStudentIds(projectId, range, target);

        ScoreSegmentCounter scoreSegments = getScoreSegments(projectId, target, studentIds);
        saveScoreSegments(projectId, range, target, scoreSegments);
    }

    // 保存成绩分段
    private void saveScoreSegments(String projectId, Range range, Target target, ScoreSegmentCounter counter) {
        Document query = Mongo.query(projectId, range, target);
        Document update = $set("scoreSegments", counter.toDocuments());
        scoreDatabase.getCollection("score_segment").updateOne(query, update, UPSERT);
    }

    // 生成成绩分段
    private ScoreSegmentCounter getScoreSegments(String projectId, Target target, List<String> studentIds) {
        ScoreSegmentCounter counter = new ScoreSegmentCounter(getInterval(target));

        for (String studentId : studentIds) {
            double score = scoreService.getScore(projectId, Range.student(studentId), target);
            counter.addScore(score);
        }
        return counter;
    }

    // 判断成绩分段大小
    private int getInterval(Target target) {
        if (target.match(Target.PROJECT)) {
            return 50;
        } else if (target.match(Target.SUBJECT) || target.match(Target.SUBJECT_OBJECTIVE)) {
            return 10;
        } else {
            throw new IllegalArgumentException("非法的 Target 类型：" + target);
        }
    }
}
