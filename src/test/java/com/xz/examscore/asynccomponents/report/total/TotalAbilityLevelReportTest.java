package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/3/22.
 */
public class TotalAbilityLevelReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalAbilityLevelReport totalAbilityLevelReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        totalAbilityLevelReport.generate("430200-9583fddde42d42b2b480b1c5c8cdaf82",
                null,
                "target/total-abilityLevel.xlsx");
    }
}