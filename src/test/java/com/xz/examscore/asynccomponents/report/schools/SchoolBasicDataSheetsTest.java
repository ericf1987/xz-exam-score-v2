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
        schoolBasicDataReport.generate("430100-5d2142085fc747c9b5b230203bbfd402", Range.school("d988de7f-8a44-487c-9442-449c90dfd861"), "target/school_basic_data.xlsx");

    }
}