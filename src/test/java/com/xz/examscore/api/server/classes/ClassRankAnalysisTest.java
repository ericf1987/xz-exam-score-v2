package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/1.
 */
public class ClassRankAnalysisTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassRankAnalysis classRankAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-2df3f3ad199042c39c5f4b69f5dc7840")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0")
                .setParameter("classId", "a1895cd9-d82c-4b12-a698-164fb5ceb1f3");
        Result result = classRankAnalysis.execute(param);
        System.out.println(result.getData());
    }
}