package com.xz.examscore.api.server.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/1.
 */
public class ClassPointAnalysisTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassPointAnalysis classPointAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        String classId = "a110e9c5-3b76-46a2-9de6-c1dd34769e37";
        String subjectId = "004";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("classId", classId)
                .setParameter("subjectId", subjectId);
        System.out.println(classPointAnalysis.execute(param).getData());
    }
}