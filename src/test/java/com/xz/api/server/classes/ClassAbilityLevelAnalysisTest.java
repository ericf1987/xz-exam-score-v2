package com.xz.api.server.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/08/23
 *
 * @author yiding_he
 */
public class ClassAbilityLevelAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassAbilityLevelAnalysis classAbilityLevelAnalysis;

    @Test
    public void execute() throws Exception {
        classAbilityLevelAnalysis.execute(new Param()
                .setParameter("projectId", "FAKE_PROJ_1471937125364_0")
                .setParameter("subjectId", "001")
                .setParameter("classId", "CLASS_1471937125373_2")
        );
    }

}