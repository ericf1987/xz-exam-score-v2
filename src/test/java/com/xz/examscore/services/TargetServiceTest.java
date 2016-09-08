package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Target;
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

    public static final String PROJECT = "430100-a05db0d05ad14010a5c782cd31c0283f";

    @Autowired
    TargetService targetService;

    @Test
    public void testQueryTargets() throws Exception {
        List<Target> targets = targetService.queryTargets(PROJECT, Target.SUBJECT_LEVEL);
        targets.forEach(System.out::println);
    }

    @Test
    public void testGetTargetSubject() throws Exception {
        String subjectId = targetService.getTargetSubjectId(PROJECT, Target.subjectLevel("003", "A"));
        System.out.println(subjectId);
    }
}