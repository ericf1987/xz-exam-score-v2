package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
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
        List<Range> ranges = rangeService.queryRanges(
                "430100-2c641a3e36ff492aa535da7fb4cf28cf", Range.STUDENT);
        ranges.forEach(System.out::println);
    }

    @Test
    public void testQueryProvinceRange() throws Exception {
        List<Range> ranges = rangeService.queryRanges("430200-89c9dc7481cd47a69d85af3f0808e0c4", Range.PROVINCE);
        System.out.println(ranges);
    }
}