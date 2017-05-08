package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/2/19.
 */
public class SchoolQuestScoreDetailAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolQuestScoreDetailAnalysis schoolQuestScoreDetailAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "FAKE_PROJ_1486524671547_0")
                .setParameter("schoolId", "SCHOOL_1486524671577_1")
                .setParameter("subjectId", "001");
        Result result = schoolQuestScoreDetailAnalysis.execute(param);
        long begin = System.currentTimeMillis();
        System.out.println(result.getData());
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - begin));
    }
}