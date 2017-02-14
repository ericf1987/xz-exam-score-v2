package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/1.
 */
public class SchoolPointAbilityLevelSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolPointAbilityLevelReport schoolPointAbilityLevelReport;

    @Test
    public void testGenerateSheet() throws Exception {
        long begin = System.currentTimeMillis();
        schoolPointAbilityLevelReport.generate("430100-e7bd093d92d844819c7eda8b641ab6ee", Range.school("d00faaa0-8a9b-45c4-ae16-ea2688353cd0"), "target/school-point-ability-level-new.xlsx");
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }
}