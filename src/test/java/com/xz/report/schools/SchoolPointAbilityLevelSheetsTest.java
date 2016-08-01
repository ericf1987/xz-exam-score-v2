package com.xz.report.schools;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/1.
 */
public class SchoolPointAbilityLevelSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolPointAbilityLevelReport schoolPointAbilityLevelReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolPointAbilityLevelReport.generate("430100-2df3f3ad199042c39c5f4b69f5dc7840", Range.school("d00faaa0-8a9b-45c4-ae16-ea2688353cd0"), "target/school-point-ability-level.xlsx");
    }
}