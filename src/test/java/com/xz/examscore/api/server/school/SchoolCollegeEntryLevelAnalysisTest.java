package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/24.
 */
public class SchoolCollegeEntryLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolCollegeEntryLevelAnalysis schoolCollegeEntryLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "433100-fef19389d6ce4b1f99847ab96d2cfeba";
        String schoolId = "64a1c8cd-a9b9-4755-a973-e1ce07f3f70a";
        String[] rankSegment = new String[]{
                "1", "5"
        };
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("rankSegment", rankSegment);

        Result result = schoolCollegeEntryLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}