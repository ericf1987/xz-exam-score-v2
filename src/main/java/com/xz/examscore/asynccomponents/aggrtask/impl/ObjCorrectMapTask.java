package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.Mongo.query;

@AggrTaskMeta(taskType = "obj_correct_map")
@Component
public class ObjCorrectMapTask extends AggrTask {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    TargetService targetService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();
        String questId = target.getId().toString();
        String subject = targetService.getTargetSubjectId(projectId, target);

        int studentCount = studentService.getStudentCount(projectId, subject, range);
        int correctCount = scoreService.getQuestCorrentCount(projectId, questId, range);

        double correctRate = studentCount == 0 ? 0 : ((double) correctCount / studentCount);
        scoreDatabase.getCollection("obj_correct_map").updateOne(
                query(projectId, range, target),
                $set(doc("correctCount", correctCount).append("correctRate", correctRate)),
                UPSERT
        );
    }
}
