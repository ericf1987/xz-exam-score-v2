package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Test
    public void testGetRangesMap() throws Exception {
        long begin = System.currentTimeMillis();
        Map<String, List<Range>> map = rangeService.getRangesMap("430500-60e161a4963b4fbe899d23d4be28b253");
        long end = System.currentTimeMillis();
        System.out.println("统计维度耗时：" + (end - begin));
        Set<String> keySet = map.keySet();
        for (String key : keySet){
            List<Range> list = map.get(key);
            System.out.println("key:" + key + ", size:" + list.size());
        }
    }
}