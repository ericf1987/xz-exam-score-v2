package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskInfo;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/26
 *
 * @author yiding_he
 */
public class TscoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TscoreTask tscoreTask;

    @Test
    public void testRunTask() throws Exception {
        tscoreTask.runTask(new AggrTaskInfo(XT_PROJECT_ID, "111", "t_score")
                .setTarget(Target.subject("001")).setRange(Range.school("002e02d6-c036-4780-85d4-e54e3f1fbf9f")));
    }
}