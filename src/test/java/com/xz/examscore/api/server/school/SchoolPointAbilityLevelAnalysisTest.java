package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.services.AbilityLevelService;
import com.xz.examscore.services.ProjectService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/2/13.
 */
public class SchoolPointAbilityLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolPointAbilityLevelAnalysis schoolPointAbilityLevelAnalysis;
    
    @Autowired
    ProjectService projectService;
    
    @Autowired
    AbilityLevelService abilityLevelService;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-194d9c9dd59d4145ae94bb66a06434d0";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", "49d8f246-7fb8-4e95-8385-47ff17f3d013")
                .setParameter("subjectId", "001");
        Result execute = schoolPointAbilityLevelAnalysis.execute(param);
        System.out.println(execute.getData());
    }
}