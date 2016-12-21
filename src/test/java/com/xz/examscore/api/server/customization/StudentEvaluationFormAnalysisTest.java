package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/8.
 */
public class StudentEvaluationFormAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentEvaluationFormAnalysis studentEvaluationFormAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430500-ea90a33d908c40aba5907bd97b838d61";
        String schoolId = "df32d9c9-3cb0-4d1a-84ec-fd1197d3dc4c";
        String classId = "5247109a-4686-4ac2-a8e4-f9b6d6c75762";
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