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
        report.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total_basic_rank_report.xlsx");
    }
}