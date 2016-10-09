package com.xz.examscore.api.server.school;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/9.
 */
public class SchoolSubjectAnalysisTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolScoreAnalysis schoolScoreAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430600-b52cda0d90ee468094873e10ee161d4a")
                .setParameter("schoolId", "15c60ef9-b97c-48fb-bfbe-4c2c24bbea33")
                .setParameter("subjectId", "001");
        System.out.println(schoolScoreAnalysis.execute(param).getData());
    }
}