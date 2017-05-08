package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.intclient.InterfaceClient;
import com.xz.examscore.services.ImportProjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/1/5.
 */
public class TotalQuestAbilityLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalQuestAbilityLevelAnalysis totalQuestAbilityLevelAnalysis;

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    InterfaceClient interfaceClient;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430300-57b0625497644b8faf878045ea0c6439");
        Result result = totalQuestAbilityLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void test2() throws Exception {
        String projectId = "430300-6f2f82b0ea1a4dcca29f692570eabb50";
        Map<String, Object> map = interfaceClient.queryQuestionByProject(projectId, true);
        Map<String, Object> optionalQuestNo = importProjectService.getOptionalQuestNo(map);
        List<String> o = (List<String>)optionalQuestNo.get("008");
        totalQuestAbilityLevelAnalysis.getOptionQuestScore(projectId, "008", "ability", o);
    }
}