package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/23
 *
 * @author yiding_he
 */
public class OptionMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    OptionMapTask optionMapTask;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430600-855904ddd05243d2a6eeb76832e7b61e";
        String schoolId = "d1bf6d54-1e2e-40b3-b3df-fda8069e4389";
        String questId = "57d132bb2de04306ae9f124a";
        optionMapTask.runTask(new AggrTaskMessage(projectId, "11", "option_count")
                .setRange(Range.school(schoolId)).setTarget(Target.quest(questId)));
    }

    @Test
    public void testAggrClassOptionCount() throws Exception {
        String projectId = "430600-855904ddd05243d2a6eeb76832e7b61e";
        String classId = "1563ee2a-61cb-41e5-839a-b2cc09ea54a5";
        String questId = "57d132bb2de04306ae9f124a";
        optionMapTask.runTask(new AggrTaskMessage(projectId, "11", "option_count")
                .setRange(Range.clazz(classId)).setTarget(Target.quest(questId)));
    }
}