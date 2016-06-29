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
                .setParameter("projectId", "430300-672a0ed23d9148e5a2a31c8bf1e08e62")
                .setParameter("subjectId", "001")
                .setParameter("schoolIds", "0835e05b-4d01-4944-9a0a-b8a77f201933"));

        System.out.println(JSON.toJSONString(result));
    }
}