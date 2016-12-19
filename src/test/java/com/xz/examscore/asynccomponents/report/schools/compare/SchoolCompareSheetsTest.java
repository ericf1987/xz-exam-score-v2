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
        schoolCompareReport.generate("430200-3730c73573a842b1aace1fcbdb6c087d", Range.school("64b32035-baa7-461b-bc7f-edab34d9f5b3"), "target/school-compare.xlsx");
    }
}