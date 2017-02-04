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
        Param param = new Param().setParameter("projectId", "430300-57b0625497644b8faf878045ea0c6439")
                .setParameter("schoolId", "10ac7bba-8813-4268-9c3e-0970280ca78c");
        Result result = schoolCombinedRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}