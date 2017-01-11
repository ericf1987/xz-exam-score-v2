package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/1/11.
 */
public class TotalScoreMinAboveReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreMinAboveReport totalScoreMinAboveReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        totalScoreMinAboveReport.generate("430100-501b96776dc348748e2afdb95d491516", null, "target/学校分数段累积统计（10分段）.xlsx");
    }
}