package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
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
        report.generate(UNION_PROJECT_ID, Range.province("430000"), "target/total_basic_rank.xlsx");
    }
}