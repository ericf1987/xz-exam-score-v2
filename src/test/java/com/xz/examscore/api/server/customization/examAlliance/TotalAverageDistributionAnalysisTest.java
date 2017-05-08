package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/26.
 */
public class TotalAverageDistributionAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalAverageDistributionAnalysis totalAverageDistributionAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430300-672a0ed23d9148e5a2a31c8bf1e08e62");
        Result result = totalAverageDistributionAnalysis.execute(param);
        System.out.println(result.getData());
    }
}