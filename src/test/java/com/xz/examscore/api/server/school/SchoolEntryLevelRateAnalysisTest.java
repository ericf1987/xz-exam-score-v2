package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
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
        String projectId = "430100-1944e9f7048b48e2b38e35db75be4980";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        List<Document> classDocs = classService.listClasses(projectId, schoolId);
        classDocs.forEach(doc -> System.out.println(doc.getString("name")));

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId);
        Result result = schoolEntryLevelRateAnalysis.execute(param);
        System.out.println(result.getData());
    }
}