package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/17.
 */
public class TotalPaperQuestTypeSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalPaperQuestTypeReport totalPaperQuestTypeReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalPaperQuestTypeReport.generate("430100-2df3f3ad199042c39c5f4b69f5dc7840", Range.province("430000"), "target/total-paper-quest-type.xlsx");
    }
}