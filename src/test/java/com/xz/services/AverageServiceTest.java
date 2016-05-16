package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

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
        double average = averageService.getAverage(PROJECT_ID,
                Range.clazz("SCHOOL_004_CLASS_04"), Target.quest("5732e1f6c5a637047a2f43f8"));

        System.out.println(average);
    }
}