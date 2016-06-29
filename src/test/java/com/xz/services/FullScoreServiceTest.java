package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/23
 *
 * @author yiding_he
 */
public class FullScoreServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    FullScoreService fullScoreService;

    @Test
    public void testGetFullScore() throws Exception {
        double fullScore = fullScoreService.getFullScore(UNION_PROJECT_ID, Target.subjectLevel("001", "A"));
        System.out.println(fullScore);
    }
}