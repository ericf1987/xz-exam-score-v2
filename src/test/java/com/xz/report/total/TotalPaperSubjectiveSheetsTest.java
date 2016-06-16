package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/15.
 */
public class TotalPaperSubjectiveSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalPaperSubjectiveReport totalPaperSubjectiveReport;

    @Test
    public void testGetSheetTask() throws Exception{
        totalPaperSubjectiveReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total-paper-subjective.xlsx");
    }
}