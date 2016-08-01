package com.xz.api.server.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/21.
 */
public class ClassRankLevelAnalysisTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    ClassRankLevelAnalysis classRankLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param()
                .setParameter("projectId", "430200-b73f03af1d74484f84f1aa93f583caaa")
                .setParameter("classId", "0c738247-b62c-4c90-9016-1cc1163fd0b1");
        classRankLevelAnalysis.execute(param);
    }
}