package com.xz.examscore.asynccomponents.report.customization;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/10/17.
 */
public class AllClassScoreCompareReportTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    AllClassScoreCompareReport allClassScoreCompareReport;

    @Test
    public void testGetSheetTask() throws Exception{
        allClassScoreCompareReport.generate("430200-01ef739fb0074d489f39e62a9be64629", null, "target/all-class-score-compare.xlsx");
    }
}