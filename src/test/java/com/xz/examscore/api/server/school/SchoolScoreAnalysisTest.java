package com.xz.examscore.api.server.school;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/8/30.
 */
public class SchoolScoreAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolScoreAnalysis schoolScoreAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-cb04005aa5ae460fae6b9d87df797066")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0")
                .setParameter("subjectId", "001");
        System.out.println(schoolScoreAnalysis.execute(param).getData());
    }
}