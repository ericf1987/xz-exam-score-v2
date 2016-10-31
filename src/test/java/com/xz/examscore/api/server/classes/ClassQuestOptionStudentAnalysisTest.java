package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/31.
 */
public class ClassQuestOptionStudentAnalysisTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassQuestOptionStudentAnalysis classQuestOptionStudentAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-2e63dd5efb014a0b9a0505ad36ce8f90";
        String classId = "01041c3e-f6f9-4ee2-8d10-e525d47dde7b";
        String subjectId = "001";
        Param param = new Param().setParameter("projectId", projectId).setParameter("classId", classId).setParameter("subjectId", subjectId);
        Result result = classQuestOptionStudentAnalysis.execute(param);
        System.out.println(result.getData());
    }
}