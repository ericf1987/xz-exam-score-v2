package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/11/4.
 */
public class SchoolCollegeEntryLevelReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolCollegeEntryLevelReport schoolCollegeEntryLevelReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        schoolCollegeEntryLevelReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.school("9ea1472e-9f8e-4b48-b00c-8bde3288cc80"), "target/school-college-entry-level.xlsx");
    }
}