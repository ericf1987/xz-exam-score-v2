package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/17.
 */
public class AllClassScoreCompareTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    AllClassScoreCompare allClassScoreCompare;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Param param = new Param().setParameter("projectId", projectId);
        Result result = allClassScoreCompare.execute(param);
        System.out.println(result.getData());
    }
}