package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/13
 *
 * @author yiding_he
 */
public class TopStudentListTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TopStudentListTask topStudentListTask;

    @Test
    public void testRunTask() throws Exception {
        topStudentListTask.runTask(new AggrTaskMessage(XT_PROJECT_ID, "1", "top_student_list")
                .setTarget(Target.project(XT_PROJECT_ID)).setRange(Range.school("002e02d6-c036-4780-85d4-e54e3f1fbf9f")));
    }
}