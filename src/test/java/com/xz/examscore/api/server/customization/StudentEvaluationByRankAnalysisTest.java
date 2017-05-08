package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/4/6.
 */
public class StudentEvaluationByRankAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentEvaluationByRankAnalysis studentEvaluationByRankAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430300-f39dc20ba0044b8b9cb302d7910c87c4";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("pageSize", 10)
                .setParameter("pageCount", 3);

        Result result = studentEvaluationByRankAnalysis.execute(param);
        System.out.println(result.getData());
    }
}