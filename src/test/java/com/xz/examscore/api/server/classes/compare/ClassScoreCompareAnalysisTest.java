package com.xz.examscore.api.server.classes.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/26.
 */
public class ClassScoreCompareAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassScoreCompareAnalysis classScoreCompareAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param()
                .setParameter("projectId", "430100-e7bd093d92d844819c7eda8b641ab6ee")
                .setParameter("subjectId", "001")
                .setParameter("classId", "048eb56f-a105-4992-8228-0e436c9e4670");
        Result result = classScoreCompareAnalysis.execute(param);
        System.out.println(result.getData());
    }
}