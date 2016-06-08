package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * (description)
 * created at 16/06/08
 *
 * @author yiding_he
 */
public class RankSegmentServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankSegmentService rankSegmentService;

    @Test
    public void testQueryFullRankSegment() throws Exception {
        List<Map<String, Object>> maps = rankSegmentService.queryFullRankSegment(
                PROJECT_ID,
                Target.subject("001"),
                Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47"));

        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }
    }
}