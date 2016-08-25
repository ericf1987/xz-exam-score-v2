package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/13
 *
 * @author yiding_he
 */
public class QuestTypeScoreServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Test
    public void testGetQuestTypeScore() throws Exception {
        String questTypeId = "01a60868-f0c7-453f-8ece-9d766d3aa90a";
        double score = questTypeScoreService.getQuestTypeScore(XT_PROJECT_ID, Range.area("430301"), questTypeId);
        System.out.println(score);
    }
}