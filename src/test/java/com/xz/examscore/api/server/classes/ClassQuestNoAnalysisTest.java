package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/11/1.
 */
public class ClassQuestNoAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassQuestNoAnalysis classQuestNoAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-2e63dd5efb014a0b9a0505ad36ce8f90")
                .setParameter("classId", "24d60ab6-485e-4ff2-a5c5-d5921f7eeb57")
                .setParameter("subjectId", "001");
        Result result = classQuestNoAnalysis.execute(param);
        System.out.println(result.getData());
    }
}