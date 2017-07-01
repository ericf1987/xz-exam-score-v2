package com.xz.examscore.asynccomponents.aggrtask.impl.totalscore;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/3/31.
 */
public class TotalScoreStudentTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreStudentTask totalScoreStudentTask;

    @Autowired
    private MongoDatabase scoreDatabase;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430100-354dce3ac8ef4800a1b57f81a10b8baa";
        String studentId = "5ba113bf-a635-4636-b6fa-4461347d0d37";
        String subjectId = "004";

        totalScoreStudentTask.runTask(new AggrTaskMessage(projectId, "1", "total_score_student").setRange(Range.student(studentId))
        .setTarget(Target.subject("004")));
    }

    @Test
    public void test1() throws Exception {
        String projectId = "430300-6c03c0facc9244f88ab819368d2b95f1";
        String studentId = "6502f4bf-d44a-4011-9f18-b2d50d868c25";
        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");

        totalScoreStudentTask.aggrStudentSubjectProjectScores(projectId, Target.project(projectId), scoreCollection, Range.student(studentId));
    }

}