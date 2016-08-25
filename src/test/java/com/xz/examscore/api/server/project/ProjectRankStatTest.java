package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * (description)
 * created at 16/06/08
 *
 * @author yiding_he
 */
public class ProjectRankStatTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectRankStat projectRankStat;

    @Test
    public void testExecute() throws Exception {
        Result result = projectRankStat.execute(new Param()
                .setParameter("projectId", XT_PROJECT_ID)
                .setParameter("subjectId", "001")
                .setParameter("schoolIds", "11b66fc2-8a76-41c2-a1b3-5011523c7e47"));

        List<Map<String, Object>> schools = result.get("schools");

        for (Map<String, Object> school : schools) {
            System.out.println(school);
        }
    }
}