package com.xz.api.server.project;

import com.alibaba.fastjson.JSON;
import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.services.SchoolService;
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
                .setParameter("projectId", "430100-2df3f3ad199042c39c5f4b69f5dc7840")
                .setParameter("subjectId", "003")
                .setParameter("schoolIds", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0"));

        System.out.println(JSON.toJSONString(result));
    }
}