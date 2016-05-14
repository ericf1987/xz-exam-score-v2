package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class ScoreServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreService scoreService;

    @Test
    public void testGetTotalScore() throws Exception {
        double score = scoreService.getScore(PROJECT_ID,
                Range.clazz("SCHOOL_004_CLASS_04"), Target.quest("5732e1f6c5a637047a2f43f8"));

        System.out.println(score);
    }
}