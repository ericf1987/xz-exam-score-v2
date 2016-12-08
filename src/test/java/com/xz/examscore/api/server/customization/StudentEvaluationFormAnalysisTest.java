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
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        String schoolId = "002e02d6-c036-4780-85d4-e54e3f1fbf9f";
        String classId = "46d626b6-9250-4a63-9191-e790ed67a789";
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