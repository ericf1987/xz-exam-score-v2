package com.xz.report.total;

import com.xz.XzExamScoreV2Application;
import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/17.
 */
public class TotalPaperQuestTypeSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalPaperQuestTypeReport totalPaperQuestTypeReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalPaperQuestTypeReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total-paper-quest-type.xlsx");
    }
}