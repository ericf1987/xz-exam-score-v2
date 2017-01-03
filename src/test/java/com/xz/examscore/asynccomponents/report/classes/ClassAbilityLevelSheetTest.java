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
        classAbilityLevelReport.generate("430200-3e67c524f149491597279ef6ae31baef", Range.clazz("b5e4cfda-932c-4003-94a9-cdfd4cc664ae"), "target/class-ability-level.xlsx");
    }
}