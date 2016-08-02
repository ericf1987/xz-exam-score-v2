package com.xz.api.server.school;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/8/1.
 */
public class SchoolTopStudentQuestTypeStatTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SchoolTopStudentQuestTypeStat schoolTopStudentQuestTypeStat;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-2df3f3ad199042c39c5f4b69f5dc7840";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        String subjectId = "003";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("subjectId", subjectId);
        Result result = schoolTopStudentQuestTypeStat.execute(param);
        System.out.println(result.getData());
    }
}