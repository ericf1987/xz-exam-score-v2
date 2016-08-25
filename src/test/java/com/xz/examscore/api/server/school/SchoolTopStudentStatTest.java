package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/1.
 */
public class SchoolTopStudentStatTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SchoolTopStudentStat schoolTopStudentStat;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-2df3f3ad199042c39c5f4b69f5dc7840";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        String subjectId = "003";
        String[] rankSegment = new String[]{
                "1", "1"
        };
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("subjectId", subjectId)
                .setParameter("rankSegment", rankSegment);

        Result result = schoolTopStudentStat.execute(param);
        System.out.println(result.getData());
    }
}