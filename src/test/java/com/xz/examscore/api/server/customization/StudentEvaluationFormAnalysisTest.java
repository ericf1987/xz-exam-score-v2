package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.services.SubjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author by fengye on 2016/12/8.
 */
public class StudentEvaluationFormAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentEvaluationFormAnalysis studentEvaluationFormAnalysis;

    @Autowired
    SubjectService subjectService;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-501b96776dc348748e2afdb95d491516";
        String schoolId = "e12f9a05-4686-435a-869c-26e82f7722e6";
        String classId = "c006d874-9d7c-483a-a37f-4d7c78c8dea4";
        String pageSize = "10";
        String pageCount = "1";
        Param param = new Param().setParameter("projectId", projectId).setParameter("schoolId", schoolId)
                .setParameter("classId", classId)
                .setParameter("pageSize", pageSize)
                .setParameter("pageCount", pageCount);
        Result result = studentEvaluationFormAnalysis.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void testIsRequiredStudent() throws Exception {
        String projectId = "430100-501b96776dc348748e2afdb95d491516";
        String studentId = "00106a2b-8114-4c07-bc3f-26b3016f65b8";
        List<String> subjectIds = subjectService.querySubjects(projectId);
        boolean requiredStudent = studentEvaluationFormAnalysis.isRequiredStudent(projectId, studentId, subjectIds);
        System.out.println(requiredStudent);
    }
}