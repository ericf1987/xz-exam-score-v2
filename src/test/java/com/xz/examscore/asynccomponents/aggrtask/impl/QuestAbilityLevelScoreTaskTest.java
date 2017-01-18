package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskManager;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/1/4.
 */
public class QuestAbilityLevelScoreTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestAbilityLevelScoreTask questAbilityLevelScoreTask;

    @Test
    public void testRunTask() throws Exception {
        AggrTaskMessage atm = new AggrTaskMessage();
        atm.setProjectId("430200-3e67c524f149491597279ef6ae31baef");
        atm.setRange(Range.student("0169a905-418f-4d51-8ce2-90788bd481d5"));
        atm.setAggregationId("09787896123");
        atm.setType("quest_ability_level_score");
        questAbilityLevelScoreTask.runTask(atm);
    }

    @Test
    public void testdoQuestAbilityLevelTaskDistribute() throws Exception {
        String projectId = "430300-6f2f82b0ea1a4dcca29f692570eabb50";
        String studentId = "5e32451a-0a39-4abb-acda-2fc519527399";
        String subjectId = "001";
        Map<String, Double> map = new HashMap<>();
        questAbilityLevelScoreTask.doQuestAbilityLevelTaskDistribute(projectId, subjectId, studentId, map);
    }
}