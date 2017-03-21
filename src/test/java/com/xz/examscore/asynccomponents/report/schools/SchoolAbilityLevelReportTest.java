package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/3/20.
 */
public class SchoolAbilityLevelReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolAbilityLevelReport schoolAbilityLevelReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        schoolAbilityLevelReport.generate("430200-9583fddde42d42b2b480b1c5c8cdaf82",
                Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52"),
                "target/school-abilityLevel.xlsx");
    }
}