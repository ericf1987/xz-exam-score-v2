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
                Range.student("SCHOOL_009_CLASS_03_02"),
                Target.subject("004"));

        System.out.println(score);
    }
}