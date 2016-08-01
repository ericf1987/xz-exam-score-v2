package com.xz.api.server.classes.compare;

import com.xz.XzExamScoreV2ApplicationTests;
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
                .setParameter("projectId", "430200-b73f03af1d74484f84f1aa93f583caaa")
                .setParameter("subjectId", "001")
                .setParameter("classId", "0c738247-b62c-4c90-9016-1cc1163fd0b1");
        classScoreCompareAnalysis.execute(param);
    }
}