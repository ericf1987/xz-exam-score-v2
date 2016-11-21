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
        Param param = new Param().setParameter("projectId", "430100-bdfa63b0070b431fbc5308c0d362e74f")
                .setParameter("schoolId", "d9bdecc9-0185-4688-90d1-1aaf27e2dcfd")
                .setParameter("classId", "ab2fba9c-2bfe-4ef6-a3f0-ce62247b24ec");
        Result result = classRankAnalysis.execute(param);
        System.out.println(result.getData());
    }
}