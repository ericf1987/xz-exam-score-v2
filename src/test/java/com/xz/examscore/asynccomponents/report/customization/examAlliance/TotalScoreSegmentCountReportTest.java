package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.server.customization.examAlliance.TotalScoreSegmentCountAnalysis;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/6.
 */
public class TotalScoreSegmentCountReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreSegmentCountReport totalScoreSegmentCountReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        totalScoreSegmentCountReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", null, "target/分数段表.xlsx");
    }
}