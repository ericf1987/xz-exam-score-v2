package com.xz.examscore.api.server.school.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/26.
 */
public class SchoolCompareAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolCompareAnalysis schoolCompareAnalysis;

    @Test
    public void testExecute() throws Exception {
        String project = "433100-148ec5544f7b4764851c3a8976945a2f";
        String schoolId = "64a1c8cd-a9b9-4755-a973-e1ce07f3f70a";
        String subjectId = "";
        Param param = new Param().setParameter("projectId", project)
                .setParameter("schoolId", schoolId)
                .setParameter("subjectId", subjectId);
        Result result = schoolCompareAnalysis.execute(param);
        System.out.println(result.getData());
    }
}