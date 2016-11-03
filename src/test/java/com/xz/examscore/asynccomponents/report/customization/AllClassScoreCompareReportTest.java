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
        allClassScoreCompareReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", null, "target/all-class-score-compare.xlsx");
    }
}