package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
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
        Param param = new Param().setParameter("projectId", "430200-fe3b4d91bb804c249b5b97a8d95c3709")
                .setParameter("schoolId", "55c3e888-7f90-4d6d-977d-d0bef861d6a9");
        Result result = schoolCombinedRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}