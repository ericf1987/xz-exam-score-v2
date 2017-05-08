package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/20.
 */
public class ClassCombinedRankLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassCombinedRankLevelAnalysis classCombinedRankLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430300-32d8433951ce43cab5883abff77c8ea3")
                .setParameter("classId", "f33ab424-ec1e-4f55-a45d-47ad84faa796");
        Result result = classCombinedRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}