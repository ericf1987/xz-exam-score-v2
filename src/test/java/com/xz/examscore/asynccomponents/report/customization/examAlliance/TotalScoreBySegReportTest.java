package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/1/10.
 */
public class TotalScoreBySegReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreBySegReport totalScoreBySegReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        totalScoreBySegReport.generate("430100-501b96776dc348748e2afdb95d491516", null, "target/学校分数分段统计（10分段）.xlsx");
    }
}