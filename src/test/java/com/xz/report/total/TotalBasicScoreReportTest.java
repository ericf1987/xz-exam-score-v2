package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
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
        totalBasicScoreReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total-basic-score.xlsx");
    }
}