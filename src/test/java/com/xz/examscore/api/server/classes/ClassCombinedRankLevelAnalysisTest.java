package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
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
        Param param = new Param().setParameter("projectId", "430600-2404b0cc131c472dbbd13085385f5ee0")
                .setParameter("classId", "e86f50b4-cbe6-403c-84d1-8cc668ee0221");
        Result result = classCombinedRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}