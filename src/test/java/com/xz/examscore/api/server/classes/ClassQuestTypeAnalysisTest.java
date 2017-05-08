package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
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
        Param param = new Param().setParameter("projectId", "430600-262ccb128212420b95977d21601796ee")
                .setParameter("classId", "67374c07-1576-4857-b978-95bcd8640749")
                .setParameter("subjectId", "001");
        long begin = System.currentTimeMillis();
        Result result = classQuestTypeAnalysis.execute(param);
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
        System.out.println(result.getData());
    }
}