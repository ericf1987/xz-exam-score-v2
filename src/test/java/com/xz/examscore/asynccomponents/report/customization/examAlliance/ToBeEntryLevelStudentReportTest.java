package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.server.customization.examAlliance.ToBeEntryLevelStudentAnalysis;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/28.
 */
public class ToBeEntryLevelStudentReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ToBeEntryLevelStudentReport toBeEntryLevelStudentReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        toBeEntryLevelStudentReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", null, "target/临界生人数及各科得分率.xlsx");
    }
}