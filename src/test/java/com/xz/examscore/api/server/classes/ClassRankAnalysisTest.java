package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/1.
 */
public class ClassRankAnalysisTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassRankAnalysis classRankAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430600-95e565c247574dd3b935ae9912c8eca5")
                .setParameter("schoolId", "d1bf6d54-1e2e-40b3-b3df-fda8069e4389")
                .setParameter("classId", "649e603f-1e27-43bb-89ca-5970efb76710");
        Result result = classRankAnalysis.execute(param);
        System.out.println(result.getData());
    }
}