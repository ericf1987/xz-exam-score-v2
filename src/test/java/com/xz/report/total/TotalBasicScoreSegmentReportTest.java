package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/08
 *
 * @author yiding_he
 */
public class TotalBasicScoreSegmentReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalBasicScoreSegmentReport totalBasicScoreSegmentReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        totalBasicScoreSegmentReport.generate(
                "430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total_basic_score_segment.xlsx");
    }
}