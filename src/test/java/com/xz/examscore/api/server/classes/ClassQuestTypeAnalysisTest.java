package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/11/21.
 */
public class ClassQuestTypeAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassQuestTypeAnalysis classQuestTypeAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-194d9c9dd59d4145ae94bb66a06434d0")
                .setParameter("classId", "33af690e-a3a2-41e5-b689-0fff6ebb315e")
                .setParameter("subjectId", "001");
        Result result = classQuestTypeAnalysis.execute(param);
        System.out.println(result.getData());
    }
}