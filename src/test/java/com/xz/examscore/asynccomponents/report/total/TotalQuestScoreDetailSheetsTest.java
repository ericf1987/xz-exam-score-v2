package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/1.
 */
public class TotalQuestScoreDetailSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalQuestScoreDetailReport totalQuestScoreDetailReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalQuestScoreDetailReport.generate("FAKE_PROJ_1486524671547_0", Range.province("430000"), "target/total-quest-score-detail.xlsx");
    }
}