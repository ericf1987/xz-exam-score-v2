package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/20.
 */
public class SchoolCombinedRankLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolCombinedRankLevelAnalysis schoolCombinedRankLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430600-2404b0cc131c472dbbd13085385f5ee0")
                .setParameter("schoolId", "d1bf6d54-1e2e-40b3-b3df-fda8069e4389");
        Result result = schoolCombinedRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}