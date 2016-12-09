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
        averageByRankLineReport.generate("430100-f00975f88b4e4881925613b2a238673f", null, "target/前百分段名平均分.xlsx");

    }
}