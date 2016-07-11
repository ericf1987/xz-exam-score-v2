package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class AverageServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageService averageService;

    @Test
    public void testGetAverage() throws Exception {
        double average = averageService.getAverage(UNION_PROJECT_ID,
                Range.school("0835e05b-4d01-4944-9a0a-b8a77f201933"), Target.subjectLevel("001", "A"));

        System.out.println(average);
    }

    @Test
    public void testGetAverage2() throws Exception {
        String project = "430200-b73f03af1d74484f84f1aa93f583caaa";
        Range range = Range.clazz("0c738247-b62c-4c90-9016-1cc1163fd0b1");
        Target target = Target.point("1005983");
        double average = averageService.getAverage(project, range, target);
        System.out.println(average);
    }
}