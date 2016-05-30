package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * ${描述}
 *
 * @author zhaorenwu
 */
public class ScoreSegmentServiceTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT_ID = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
    public static final String CLAZZ = "27bbbb59-06b7-4ae7-82f2-cf00d0f417e0";
    public static final String SUBJECT = "003";

    @Autowired
    ScoreSegmentService scoreSegmentService;

    @Test
    public void testQueryFullScoreSegment() throws Exception {
        List<Map<String, Object>> scoreSegmentList = scoreSegmentService.queryFullScoreSegment(
                PROJECT_ID, Target.project(PROJECT_ID), Range.clazz(CLAZZ));

        for (Map<String, Object> map : scoreSegmentList) {
            System.out.println(map.toString());
        }
    }
}