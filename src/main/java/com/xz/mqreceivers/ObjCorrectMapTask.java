package com.xz.mqreceivers;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import com.xz.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.util.Mongo.query;

@ReceiverInfo(taskType = "obj_correct_map")
@Component
public class ObjCorrectMapTask extends Receiver {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    TargetService targetService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();
        String questId = target.getId().toString();
        String subject = targetService.getTargetSubjectId(projectId, target);

        int studentCount = studentService.getStudentCount(projectId, subject, range);
        int correctCount = scoreService.getQuestCorrentCount(projectId, questId, range);

        double correctRate = (double) correctCount / studentCount;
        scoreDatabase.getCollection("obj_correct_map").updateOne(
                query(projectId, range, target),
                $set(doc("correctCount", correctCount).append("correctRate", correctRate)),
                UPSERT
        );
    }
}
