package com.xz.examscore.asynccomponents.aggrtask.impl.totalscore;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/3/31.
 */
public class TotalScoreStudentTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreStudentTask totalScoreStudentTask;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430200-cc721d3beb924d2997fe112c767b3a28";
        String studentId = "1f2cd3a4-e251-4718-b4fe-798835ded0bb";

        totalScoreStudentTask.runTask(new AggrTaskMessage(projectId, "1", "total_score_student").setRange(Range.student(studentId))
        .setTarget(Target.project(projectId)));
    }

    //测试保存总分
    @Test
    public void testaggrStudentProjectScores() throws Exception {
        String projectId = "430100-354dce3ac8ef4800a1b57f81a10b8baa";
        Range studentRange = Range.student("5ba113bf-a635-4636-b6fa-4461347d0d37");
        Target projectTarget = Target.project(projectId);
        totalScoreStudentTask.aggrStudentProjectScores(projectId, projectTarget, studentRange);
    }
}