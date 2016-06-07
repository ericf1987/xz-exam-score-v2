package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/05
 *
 * @author yiding_he
 */
public class TotalBasicRankReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalBasicRankReport report;

    @Test
    public void testGenerateReport() throws Exception {
        report.generate(PROJECT_ID, Range.province("430000"), "target/report.xlsx");
    }
}