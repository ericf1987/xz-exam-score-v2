package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
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
public class ScoreServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreService scoreService;

    @Test
    public void testGetTotalScore() throws Exception {
        double score = scoreService.getScore("430200-b73f03af1d74484f84f1aa93f583caaa",
                Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700"),
                Target.point("1025605"));

        System.out.println(score);
    }

    @Test
    public void testGetQuestCorrectCount() throws Exception {
        int count = scoreService.getQuestCorrentCount(
                "430200-89c9dc7481cd47a69d85af3f0808e0c4",
                "57403a032d560287556b90d4",
                Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52")
        );

        System.out.println(count);
    }
}