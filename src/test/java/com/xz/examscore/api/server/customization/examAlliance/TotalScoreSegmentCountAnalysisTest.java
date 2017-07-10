package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/12/3.
 */
public class TotalScoreSegmentCountAnalysisTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalScoreSegmentCountAnalysis totalScoreSegmentCountAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430300-32d8433951ce43cab5883abff77c8ea3")
                .setParameter("max", "650")
                .setParameter("min", "350")
                .setParameter("span", "10");
        Result result = totalScoreSegmentCountAnalysis.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void test2() throws Exception {
        CounterMap<Integer> scoreSegmentMap = totalScoreSegmentCountAnalysis.getScoreSegmentMap("430400-5f10b483b3574835a54d7d6702524a27", Range.school("06a272b2-08ac-4618-9f03-1f05a28bc48e"), 900, 300, 100);
        System.out.println(scoreSegmentMap.toString());
    }
}