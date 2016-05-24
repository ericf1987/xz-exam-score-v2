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
        String project = "430200-8a9be9fc2e1842a4b9b4894eee1f5f73";
        double fullScore = fullScoreService.getFullScore(project, Target.project(project));
        System.out.println(fullScore);
    }
}