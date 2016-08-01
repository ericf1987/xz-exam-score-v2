package com.xz.api.server.school.compare;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/26.
 */
public class SchoolPassCompareAnalysisTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    SchoolPassCompareAnalysis schoolPassCompareAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param()
                .setParameter("projectId", "430200-b73f03af1d74484f84f1aa93f583caaa")
                .setParameter("schoolId", "200f3928-a8bd-48c4-a2f4-322e9ffe3700")
                .setParameter("subjectId", "001");
        schoolPassCompareAnalysis.execute(param);
    }
}