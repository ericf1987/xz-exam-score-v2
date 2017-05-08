package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
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
        Param param = new Param().setParameter("projectId", "430100-194d9c9dd59d4145ae94bb66a06434d0")
                .setParameter("schoolId", "742bc2bc-5375-4281-9e66-0632a1a4c9dd")
                .setParameter("classId", "ab183e62-5bf5-4c5f-b9a3-17c003defaca");
        Result result = classRankAnalysis.execute(param);
        System.out.println(result.getData());
    }
}