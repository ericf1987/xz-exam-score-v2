package com.xz.api.server.project;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/06/08
 *
 * @author yiding_he
 */
public class ProjectScoreAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectScoreAnalysis projectScoreAnalysis;

    @Autowired
    SchoolService schoolService;

    @Test
    public void testExecute() throws Exception {
/*        String schoolId = "11b66fc2-8a76-41c2-a1b3-5011523c7e47";

        Param param = new Param()
                .setParameter("projectId", XT_PROJECT_ID)
                .setParameter("subjectId", (String) null)
                .setParameter("schoolIds", schoolId);

        Result result = projectScoreAnalysis.execute(param);

        System.out.println(result);*/

        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        String subjectId = "";
/*        Param param = new Param().setParameter("projectId", projectId).setParameter("subjectId", subjectId)
                .setParameter("isInCity", "true").setParameter("isGovernmental", "true");
        Result result = projectScoreAnalysis.execute(param);
        System.out.println(result.getData());*/
        String[] schoolIds = projectScoreAnalysis.filterByTags(projectId, null, null);
        for (String schoolId : schoolIds){
            System.out.println(schoolId);
        }
    }
}