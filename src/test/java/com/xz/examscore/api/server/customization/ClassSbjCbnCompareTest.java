package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/20.
 */
public class ClassSbjCbnCompareTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassSbjCbnCompare classSbjCbnCompare;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-2c641a3e36ff492aa535da7fb4cf28cf")
                .setParameter("subjectCombinationId", "007008009");
        Result result = classSbjCbnCompare.execute(param);
        System.out.println(result.getData());
    }
}