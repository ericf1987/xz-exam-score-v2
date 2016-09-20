package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/19.
 */
public class ClassSubjectiveAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassSubjectiveAnalysis classSubjectiveAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "433100-148ec5544f7b4764851c3a8976945a2f")
                .setParameter("classId", "f8259b31-7c8b-47ba-90d5-c5c15763660f")
                .setParameter("subjectId", "001");
        Result result = classSubjectiveAnalysis.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void testExecute1() throws Exception {

    }
}