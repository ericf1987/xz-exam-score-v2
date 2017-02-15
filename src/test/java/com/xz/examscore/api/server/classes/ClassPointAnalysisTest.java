package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/9/1.
 */
public class ClassPointAnalysisTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassPointAnalysis classPointAnalysis;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        String classId = "0bc7b0a4-adfc-4cb2-8324-863b976ab543";
        String subjectId = "001";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("classId", classId)
                .setParameter("subjectId", subjectId);
        long begin = System.currentTimeMillis();
        Result result = classPointAnalysis.execute(param);
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
        System.out.println(result.getData());
    }
}