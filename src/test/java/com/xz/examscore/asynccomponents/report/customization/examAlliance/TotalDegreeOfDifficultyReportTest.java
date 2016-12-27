package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/27.
 */
public class TotalDegreeOfDifficultyReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalDegreeOfDifficultyReport totalDegreeOfDifficultyReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        totalDegreeOfDifficultyReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", null, "target/试卷难度系数.xlsx");
    }
}