package com.xz.api.server.sys;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/8/3.
 */
public class QuerySchoolPatitionTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QuerySchoolPatition querySchoolPatition;

    @Test
    public void testExecute() throws Exception {
        System.out.println(querySchoolPatition.execute(new Param()).getData());
    }
}