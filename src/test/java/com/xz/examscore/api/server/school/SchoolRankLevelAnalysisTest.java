package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/18.
 */
public class SchoolRankLevelAnalysisTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    SchoolRankLevelAnalysis schoolRankLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0")
                .setParameter("subjectId", "");
        Result result = schoolRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}