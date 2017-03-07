package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/2/4.
 */
public class QuerySchoolProjectsTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QuerySchoolProjects querySchoolProjects;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("city", "")
                .setParameter("area", "")
                .setParameter("schoolId", "002e02d6-c036-4780-85d4-e54e3f1fbf9f")
                .setParameter("month", "");
        Result execute = querySchoolProjects.execute(param);
        System.out.println(execute.getData());
    }
}