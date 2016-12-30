package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/30.
 */
public class SubjectTopAverageReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SubjectTopAverageReport subjectTopAverageReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        subjectTopAverageReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", null, "target/各科基本情况.xlsx");
    }
}