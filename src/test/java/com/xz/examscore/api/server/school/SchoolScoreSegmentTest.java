package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/3.
 */
public class SchoolScoreSegmentTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SchoolScoreSegment schoolScoreSegment;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "FAKE_PROJ_1480250138827_0")
                .setParameter("schoolId", "SCHOOL_1480250140755_53")
                .setParameter("subjectId", "");
        Result result = schoolScoreSegment.execute(param);
        System.out.println(result.getData());
    }
}