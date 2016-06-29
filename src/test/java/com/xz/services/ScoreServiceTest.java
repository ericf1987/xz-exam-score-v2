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
        double score = scoreService.getScore(XT_PROJECT_ID,
                Range.student("SCHOOL_009_CLASS_03_02"),
                Target.subject("004"));

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