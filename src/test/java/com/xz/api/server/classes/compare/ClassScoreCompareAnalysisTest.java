package com.xz.api.server.classes.compare;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

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