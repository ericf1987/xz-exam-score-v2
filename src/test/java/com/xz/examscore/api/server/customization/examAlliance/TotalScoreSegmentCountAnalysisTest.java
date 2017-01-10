package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
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
        Param param = new Param().setParameter("projectId", "430100-501b96776dc348748e2afdb95d491516")
                .setParameter("max", "650")
                .setParameter("min", "350")
                .setParameter("span", "10");
        Result result = totalScoreSegmentCountAnalysis.execute(param);
        System.out.println(result.getData());
    }
}