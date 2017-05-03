package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
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

    public static final String PROJECT_ID = "430000-79eee8ac6c244d92a24dbcc66a2ffda2";
    public static final String CLAZZ = "27bbbb59-06b7-4ae7-82f2-cf00d0f417e0";
    public static final String SCHOOL = "43b3198c-eb0e-490a-81c5-26f4e390bd4c";
    public static final String SUBJECT = "004";

    @Autowired
    ScoreSegmentService scoreSegmentService;

    @Test
    public void testQueryFullScoreSegment() throws Exception {
        List<Map<String, Object>> scoreSegmentList = scoreSegmentService.queryFullScoreSegment(
                PROJECT_ID, Target.subject(SUBJECT), Range.school(SCHOOL));
        System.out.println(scoreSegmentList.toString());
/*        for (Map<String, Object> map : scoreSegmentList) {
            System.out.println(map.toString());
        }*/
    }
}