package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/1/4.
 */
public class QuestAbilityLevelScoreServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestAbilityLevelScoreService questAbilityLevelScoreService;

    @Test
    public void testGetTotalScore() throws Exception {
        Range range = Range.school("528654bb-3529-4ef2-9d71-5870d3f55d49");
        double totalScore = questAbilityLevelScoreService.getTotalScore("430200-3e67c524f149491597279ef6ae31baef", "006_level", null, null, range);
        System.out.println(totalScore);
        int count = questAbilityLevelScoreService.getStudentCount("430200-3e67c524f149491597279ef6ae31baef", "006_level", null, null, range);
        System.out.println(count);
    }

    @Test
    public void testGetStudentCount() throws Exception {

    }
}