package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class TargetServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TargetService targetService;

    @Test
    public void testQueryTargets() throws Exception {
        List<Target> targets = targetService.queryTargets(UNION_PROJECT_ID, Target.POINT);
        targets.forEach(System.out::println);
    }
}