package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/1/12.
 */
public class ProjectScoreAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectScoreAnalysis projectScoreAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430300-32d8433951ce43cab5883abff77c8ea3")
                .setParameter("schoolIds", new String[]{
                        "0e9e93d5-d3c9-4f53-a16d-c376d8bdf100","11b66fc2-8a76-41c2-a1b3-5011523c7e47","1b4289a9-58e2-4560-8617-27f791f956b6","2ccfc01f-4ae3-4e1a-aae6-64377a3686bb","2dbc650e-ee7f-4798-8abc-a5cc7c70268c","51cc2aee-2e9b-46a5-ac17-b07cb2d57000","fe701090-00a1-4110-9b06-7b2536570d25","002e02d6-c036-4780-85d4-e54e3f1fbf9f"
                })
                .setParameter("subjectId", "001");
        Result result = projectScoreAnalysis.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void testGetScoreAnalysisStatInfo() throws Exception {

    }
}