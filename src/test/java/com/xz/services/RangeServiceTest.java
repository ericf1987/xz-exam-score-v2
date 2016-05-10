package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class RangeServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RangeService rangeService;

    @Test
    public void testQueryRanges() throws Exception {
        List<Range> ranges = rangeService.queryRanges("FAKE_PROJECT_1",
                Range.CLASS, Range.SCHOOL, Range.AREA, Range.CITY, Range.PROVINCE);

        ranges.forEach(System.out::println);
    }
}