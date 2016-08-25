package com.xz.examscore.asynccomponents.report.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/30.
 */
public class ClassAbilityLevelSheetTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    ClassAbilityLevelReport classAbilityLevelReport;

    @Test
    public void testGenerateSheet() throws Exception {
        classAbilityLevelReport.generate("430100-8d805ef37b2f4bc7ad9808a9a109dc22", Range.clazz("e5fcaf4e-fe64-4a0e-ad43-c172c3ccd88d"), "target/class-ability-level.xlsx");
    }
}