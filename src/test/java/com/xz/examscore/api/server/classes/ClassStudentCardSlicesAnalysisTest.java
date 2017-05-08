package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/11/23.
 */
public class ClassStudentCardSlicesAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassStudentCardSlicesAnalysis classStudentCardSlicesAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-194d9c9dd59d4145ae94bb66a06434d0")
                .setParameter("studentId", "06160cba-6fac-43c6-9de4-9eec0379f4f3")
                .setParameter("subjectId", "001");
        Result result = classStudentCardSlicesAnalysis.execute(param);
        System.out.println(result.getData());
    }
}