package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
public class TotalBasicScoreReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalBasicScoreReport totalBasicScoreReport;

    @Test
    public void testGenerate() throws Exception {
        totalBasicScoreReport.generate(PROJECT_ID, "target/report.xlsx");
    }
}