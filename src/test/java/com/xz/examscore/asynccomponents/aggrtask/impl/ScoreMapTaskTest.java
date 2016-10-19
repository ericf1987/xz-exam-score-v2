package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class ScoreMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreMapTask scoreMapTask;

    @Test
    public void testRunTask() throws Exception {
        scoreMapTask.runTask(
                new AggrTaskMessage(XT_PROJECT_ID, "aaaa", "score_map")
                        .setRange(Range.clazz("46d626b6-9250-4a63-9191-e790ed67a789"))
                        .setTarget(Target.project(XT_PROJECT_ID)));
    }

    @Test
    public void testRunTask1() throws Exception {
        String projectId = "430100-2c641a3e36ff492aa535da7fb4cf28cf";
        scoreMapTask.runTask(
                new AggrTaskMessage(projectId, "1111", "score_map")
                        .setRange(Range.clazz("27bb692f-a179-41b1-a57f-ab51ee42b71d"))
                        .setTarget(Target.subjectCombination("007008009"))
        );
    }
}