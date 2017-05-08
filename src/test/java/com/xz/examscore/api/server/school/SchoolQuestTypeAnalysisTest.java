package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/11/1.
 */
public class SchoolQuestTypeAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolQuestTypeAnalysis schoolQuestTypeAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "431100-9adcc6eb14184dea982486cac233fce5";
        String schoolId = "7c23ebe1-d8c5-42a1-ba06-cd8ebffdfe6f";
        String subjectId = "001";
        Param param = new Param().setParameter("projectId", projectId).setParameter("schoolId", schoolId)
                .setParameter("subjectId", subjectId);
        Result result = schoolQuestTypeAnalysis.execute(param);
        System.out.println(result.getData());
    }
}