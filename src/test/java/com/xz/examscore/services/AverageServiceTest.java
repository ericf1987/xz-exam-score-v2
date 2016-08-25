package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
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
        // Range range = Range.province("430000");
        Range range = Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700");
        Target target = Target.point("1025548");
        double average = averageService.getAverage(project, range, target);
        System.out.println(average);
    }
}