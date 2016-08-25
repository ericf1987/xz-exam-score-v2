package com.xz.examscore.api.server.project;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.services.SchoolService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/29
 *
 * @author yiding_he
 */
public class ProjectQuestTypeAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProjectQuestTypeAnalysis projectQuestTypeAnalysis;

    @Test
    public void testExecute() throws Exception {
        Result result = projectQuestTypeAnalysis.execute(new Param()
                .setParameter("projectId", "FAKE_PROJ_1471919953702_0")
                .setParameter("subjectId", "001")
                .setParameter("schoolIds", "SCHOOL_1471919953731_1"));

        System.out.println(JSON.toJSONString(result));
    }
}