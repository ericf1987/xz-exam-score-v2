package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/28.
 */
public class ToBeEntryLevelStudentAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ToBeEntryLevelStudentAnalysis toBeEntryLevelStudentAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Param param = new Param().setParameter("projectId", projectId);
        Result result = toBeEntryLevelStudentAnalysis.execute(param);
        System.out.println(result.getData());
    }
}