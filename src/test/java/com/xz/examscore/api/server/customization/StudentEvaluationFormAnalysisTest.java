package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/12/8.
 */
public class StudentEvaluationFormAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentEvaluationFormAnalysis studentEvaluationFormAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430200-3e67c524f149491597279ef6ae31baef";
        String schoolId = "528654bb-3529-4ef2-9d71-5870d3f55d49";
        String classId = "a3fec3c6-0e46-40c3-8632-69bdf78d8484";
        String pageSize = "10";
        String pageCount = "1";
        Param param = new Param().setParameter("projectId", projectId).setParameter("schoolId", schoolId)
                .setParameter("classId", classId)
                .setParameter("pageSize", pageSize)
                .setParameter("pageCount", pageCount);
        Result result = studentEvaluationFormAnalysis.execute(param);
        System.out.println(result.getData());
    }
}