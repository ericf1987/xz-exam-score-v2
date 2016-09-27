package com.xz.examscore.asynccomponents.report.schools.compare;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/9/27.
 */
public class SchoolCompareSheetsTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolCompareReport schoolCompareReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolCompareReport.generate("433100-148ec5544f7b4764851c3a8976945a2f", Range.school("64a1c8cd-a9b9-4755-a973-e1ce07f3f70a"), "target/school-compare.xlsx");
    }
}