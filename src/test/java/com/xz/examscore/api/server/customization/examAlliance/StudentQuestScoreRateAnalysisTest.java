package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/4/10.
 */
public class StudentQuestScoreRateAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentQuestScoreRateAnalysis studentQuestScoreRateAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430300-c582131e66b64fe38da7d0510c399ec4";
        String schoolId = "15e70531-5ac0-475d-a2da-2fc04242ac75";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("subjectId", "001");
        System.out.println(studentQuestScoreRateAnalysis.execute(param).getData().toString());
    }
}