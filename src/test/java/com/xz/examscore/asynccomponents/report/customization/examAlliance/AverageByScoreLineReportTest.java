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
        averageByScoreLineReport.generate("430100-6da6fefff9b74e67917950567b368910", null, "target/800分以上人数统计.xlsx");
    }
}