package com.xz.examscore.api.server.sys;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/4.
 */
public class QueryTopStudentRankSegmentTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QueryTopStudentRankSegment queryTopStudentRankSegment;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-2df3f3ad199042c39c5f4b69f5dc7840")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0");
        System.out.println(queryTopStudentRankSegment.execute(param).getData());
    }
}