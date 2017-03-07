package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/3/6.
 */
public class ClassQuestScoreDetailAnalysisTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    ClassQuestScoreDetailAnalysis classQuestScoreDetailAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430500-858a2da0e24f4c329aafb9071e022e3b";
        String schoolId = "dab7c94b-cafa-4c92-b137-ee40cefe50ed";
        String classId = "400ae555-64ee-4689-9517-b29f0671057d";
        String subjectId = "001";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("classId", classId)
                .setParameter("subjectId", subjectId);
        Result result = classQuestScoreDetailAnalysis.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void testGetQuestListBySubject() throws Exception {

    }
}