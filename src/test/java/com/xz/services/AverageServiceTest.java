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
        double average = averageService.getAverage(XT_PROJECT_ID,
                Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47"), Target.subjectLevel("001", "A"));

        System.out.println(average);
    }
}