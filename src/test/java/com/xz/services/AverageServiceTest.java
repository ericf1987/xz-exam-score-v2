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
}