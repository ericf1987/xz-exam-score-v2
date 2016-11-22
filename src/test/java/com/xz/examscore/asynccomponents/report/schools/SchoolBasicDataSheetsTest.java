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
        schoolBasicDataReport.generate("430600-a093dba430094c76a813f72269626025", Range.school("6d3f919c-2233-450d-b3b5-a4147d131ecf"), "target/school_basic_data.xlsx");

    }
}