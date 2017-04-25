package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/20.
 */
public class SchoolCombinedRankLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolCombinedRankLevelAnalysis schoolCombinedRankLevelAnalysis;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-f779e2171766442a80afd512add13856")
                .setParameter("schoolId", "742bc2bc-5375-4281-9e66-0632a1a4c9dd");
        Result result = schoolCombinedRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
    }
}