package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/10/24.
 */
public class ProjectCollegeEntryLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectCollegeEntryLevelAnalysis projectCollegeEntryLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        String[] rankSegment = new String[]{"0", "5"};
        String projectId = "433100-fef19389d6ce4b1f99847ab96d2cfeba";
        Param param = new Param().
                setParameter("projectId", projectId).setParameter("rankSegment", rankSegment);
        Result result = projectCollegeEntryLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}