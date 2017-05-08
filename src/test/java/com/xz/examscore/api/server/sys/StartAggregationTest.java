package com.xz.examscore.api.server.sys;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/22.
 */
public class StartAggregationTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    StartAggregation startAggregation;

    @Test
    public void testExecute() throws Exception {
        String project = "433100-148ec5544f7b4764851c3a8976945a2f";
        Param param = new Param().setParameter("project", project)
                .setParameter("type", "All")
                .setParameter("reimportProject", "true")
                .setParameter("reimportScore", "true")
                .setParameter("generateReport", "true")
                .setParameter("exportScore", "true");
        System.out.println(startAggregation.execute(param).getData());
    }

    @Test
    public void testExecute1() throws Exception {

    }
}