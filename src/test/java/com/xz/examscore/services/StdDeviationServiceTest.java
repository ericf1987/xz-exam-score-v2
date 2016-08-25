package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/16
 *
 * @author yiding_he
 */
public class StdDeviationServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StdDeviationService stdDeviationService;

    @Test
    public void testGetStdDeviation() throws Exception {
        String schoolId = "002e02d6-c036-4780-85d4-e54e3f1fbf9f";
        double d = stdDeviationService.getStdDeviation(XT_PROJECT_ID, Range.province("430000"), Target.subject("003"));
        System.out.println(d);
    }
}