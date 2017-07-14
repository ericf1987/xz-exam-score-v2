package com.xz.examscore.api.server.student;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/7/11.
 */
public class QueryStudentAbsentSubjectTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QueryStudentAbsentSubject queryStudentAbsentSubject;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430200-ceb62b9fa81f47e480731d1f70e57509";

        String studentId = "6d6521a1-6aa6-4419-b6b9-342f063154da";

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("studentId", studentId);

        Result result = queryStudentAbsentSubject.execute(param);

        System.out.println(result.getData().toString());
    }
}