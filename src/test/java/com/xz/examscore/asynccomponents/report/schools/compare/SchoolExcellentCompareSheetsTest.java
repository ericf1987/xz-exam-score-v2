package com.xz.examscore.asynccomponents.report.schools.compare;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/26.
 */
public class SchoolExcellentCompareSheetsTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SchoolExcellentCompareReport schoolExcellentCompareReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolExcellentCompareReport.generate("430200-b73f03af1d74484f84f1aa93f583caaa", Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700"), "target/school-excellent-compare.xlsx");
    }
}