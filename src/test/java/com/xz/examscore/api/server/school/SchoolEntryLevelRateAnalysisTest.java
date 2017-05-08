package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.services.ClassService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author by fengye on 2016/10/25.
 */
public class SchoolEntryLevelRateAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolEntryLevelRateAnalysis schoolEntryLevelRateAnalysis;

    @Autowired
    ClassService classService;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-379be8c302a64c748f3e62cb22e5d9b5";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId);
        Result result = schoolEntryLevelRateAnalysis.execute(param);
        System.out.println(result.getData());
    }
}