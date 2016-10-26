package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/10/26.
 */
public class ClassEntryLevelRateAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassEntryLevelRateAnalysis classEntryLevelRateAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "433100-fef19389d6ce4b1f99847ab96d2cfeba";
        String classId = "f8259b31-7c8b-47ba-90d5-c5c15763660f";
        Param param = new Param().setParameter("projectId", projectId).setParameter("classId", classId);
        Result result = classEntryLevelRateAnalysis.execute(param);
        System.out.println(result.getData());
    }
}