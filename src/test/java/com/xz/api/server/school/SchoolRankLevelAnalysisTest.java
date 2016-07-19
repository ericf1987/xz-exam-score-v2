package com.xz.api.server.school;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/18.
 */
public class SchoolRankLevelAnalysisTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    SchoolRankLevelAnalysis schoolRankLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430200-b73f03af1d74484f84f1aa93f583caaa";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", "200f3928-a8bd-48c4-a2f4-322e9ffe3700")
                .setParameter("subjectId", "");
        Result result = schoolRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}