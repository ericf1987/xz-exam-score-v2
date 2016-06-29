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
        String project = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        double fullScore = fullScoreService.getFullScore(project, Target.level("A"));
        System.out.println(fullScore);
    }
}