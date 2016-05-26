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
        List<Range> ranges = rangeService.queryRanges(
                "430200-89c9dc7481cd47a69d85af3f0808e0c4", Range.CLASS, Range.SCHOOL);
        ranges.forEach(System.out::println);
    }

    @Test
    public void testQueryProvinceRange() throws Exception {
        List<Range> ranges = rangeService.queryRanges("430200-89c9dc7481cd47a69d85af3f0808e0c4", Range.PROVINCE);
        System.out.println(ranges);
    }
}