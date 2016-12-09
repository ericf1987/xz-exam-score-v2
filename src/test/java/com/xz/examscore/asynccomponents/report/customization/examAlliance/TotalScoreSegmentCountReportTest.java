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
        totalScoreSegmentCountReport.generate("430100-f00975f88b4e4881925613b2a238673f", null, "target/分数段表.xlsx");
    }
}