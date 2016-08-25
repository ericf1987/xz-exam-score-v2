package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/29
 *
 * @author yiding_he
 */
public class TotalPaperQuestTypeReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalPaperQuestTypeReport totalPaperQuestTypeReport;

    @Test
    public void testGenerate() throws Exception {
        totalPaperQuestTypeReport.generate(
                UNION_PROJECT_ID, Range.province("430000"), "target/TotalPaperQuestTypeReport.xlsx");
    }
}