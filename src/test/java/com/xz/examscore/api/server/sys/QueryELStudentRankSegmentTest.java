package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/25.
 */
public class QueryELStudentRankSegmentTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QueryELStudentRankSegment queryELStudentRankSegment;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-2e63dd5efb014a0b9a0505ad36ce8f90";
        String schoolId = "80e503de-072c-4e26-845f-271e841bf47a";
        Param param = new Param().setParameter("projectId", projectId).setParameter("schoolId", schoolId);
        Result result = queryELStudentRankSegment.execute(param);
        System.out.println(result.getData());
    }
}