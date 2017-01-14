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
        totalScoreMinAboveReport.generate("430300-32d8433951ce43cab5883abff77c8ea3", null, "target/学校分数段累积统计（10分段）.xlsx");
    }
}