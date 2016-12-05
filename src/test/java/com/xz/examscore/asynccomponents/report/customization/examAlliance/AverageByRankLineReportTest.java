package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/5.
 */
public class AverageByRankLineReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageByRankLineReport averageByRankLineReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        averageByRankLineReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", null, "target/前百分段名平均分.xlsx");

    }
}