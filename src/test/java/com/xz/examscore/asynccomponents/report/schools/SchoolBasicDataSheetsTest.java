package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/24.
 */
public class SchoolBasicDataSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolBasicDataReport schoolBasicDataReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolBasicDataReport.generate("430200-cc721d3beb924d2997fe112c767b3a28", Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700"), "target/school_basic_data.xlsx");

    }
}