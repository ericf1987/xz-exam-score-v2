package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/24.
 */
public class SchoolBasicDataAnalysisTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolBasicDataAnalysis schoolBasicDataAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "431100-903288f61a5547f1a08a7e20420c4e9e")
                .setParameter("schoolId", "b49b8e85-f390-4e09-a709-8ab1175b0c68");
        Result result = schoolBasicDataAnalysis.execute(param);
        System.out.println(result.getData());
    }
}