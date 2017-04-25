package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/4/25.
 */
public class SchoolPointAbilityLevelReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolPointAbilityLevelReport schoolPointAbilityLevelReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        schoolPointAbilityLevelReport.generate("430100-dd3013ab961946fb8a3668e5ccc475b6",
                Range.school("d9bdecc9-0185-4688-90d1-1aaf27e2dcfd"),
                "target/school-potint-ability-level.xlsx");
    }
}