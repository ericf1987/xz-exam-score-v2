package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.server.customization.examAlliance.AverageByScoreLineAnalysis;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/5.
 */
public class AverageByScoreLineReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageByScoreLineReport averageByScoreLineReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        averageByScoreLineReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", null, "target/800分以上人数统计.xlsx");
    }
}