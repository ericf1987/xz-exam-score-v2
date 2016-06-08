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
                "430200-89c9dc7481cd47a69d85af3f0808e0c4", Range.province("430000"), "target/total_basic_score_segment.xlsx");
    }
}