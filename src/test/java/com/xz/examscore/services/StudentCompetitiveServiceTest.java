package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/29.
 */
public class StudentCompetitiveServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentCompetitiveService studentCompetitiveService;

    @Test
    public void testGetAverage() throws Exception {
        String projectId = "430200-3e67c524f149491597279ef6ae31baef";
        Range classRange = Range.clazz("a3fec3c6-0e46-40c3-8632-69bdf78d8484");
        Target target = Target.subject("001");
        int rank = 10;
        System.out.println(studentCompetitiveService.getAverage(projectId, classRange, target, rank));
    }
}