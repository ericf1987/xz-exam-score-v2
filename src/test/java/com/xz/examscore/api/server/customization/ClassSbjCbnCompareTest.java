package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
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
        Param param = new Param().setParameter("projectId", "430700-caa7e02622ca402eb4a2fd071580373b");
        Result result = classSbjCbnCompare.execute(param);
        System.out.println(result.getData());
    }
}