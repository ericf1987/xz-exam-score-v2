package com.xz.examscore.api.server.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
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
                .setParameter("projectId", "430100-a05db0d05ad14010a5c782cd31c0283f")
                .setParameter("subjectId", "006")
                .setParameter("classId", "6343c455-31f8-413a-97fe-5e2ad0dd8379")
        );
    }

}