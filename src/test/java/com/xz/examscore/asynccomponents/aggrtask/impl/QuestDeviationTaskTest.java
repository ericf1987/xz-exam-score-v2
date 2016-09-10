package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/10.
 */
public class QuestDeviationTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestDeviationTask questDeviationTask;

    public static final double DEVIATION_RATE = 0.27d;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430600-855904ddd05243d2a6eeb76832e7b61e";
        String classId = "ffa38994-b119-4a39-a3b8-5bb51ff4b20b";
        String subjectId = "002";
        String questId = "57d1054a2de04364509e62b5";

        questDeviationTask.runTask(new AggrTaskMessage(projectId, "1", "quest_deviation")
                .setRange(Range.clazz(classId))
                .setTarget(Target.quest(questId)));

    }
}