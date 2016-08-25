package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
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
        totalPaperSubjectiveReport.generate("430100-8d805ef37b2f4bc7ad9808a9a109dc22", Range.province("430000"), "target/total-paper-subjective.xlsx");
    }
}