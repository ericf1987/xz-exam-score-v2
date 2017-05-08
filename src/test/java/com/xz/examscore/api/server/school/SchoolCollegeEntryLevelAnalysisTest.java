package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
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
        String projectId = "430100-1944e9f7048b48e2b38e35db75be4980";
        String schoolId = "80e503de-072c-4e26-845f-271e841bf47a";
        String[] rankSegment = new String[]{
                "1", "40"
        };
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("rankSegment", rankSegment);

        Result result = schoolCollegeEntryLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}