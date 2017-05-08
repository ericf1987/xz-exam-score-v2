package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/4.
 */
public class QuestScoreDetailAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestScoreDetailAnalysis questScoreDetailAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-f00975f88b4e4881925613b2a238673f")
                .setParameter("subjectId", "001");
        Result result = questScoreDetailAnalysis.execute(param);
        System.out.println(result.getData());
    }
}