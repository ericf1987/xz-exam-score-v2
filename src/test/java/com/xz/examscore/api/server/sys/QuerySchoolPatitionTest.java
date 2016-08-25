package com.xz.examscore.api.server.sys;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/3.
 */
public class QuerySchoolPatitionTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QuerySchoolPatition querySchoolPatition;

    @Test
    public void testExecute() throws Exception {
        System.out.println(querySchoolPatition.execute(new Param().setParameter("projectId", "430300-672a0ed23d9148e5a2a31c8bf1e08e62")).getData());
    }
}