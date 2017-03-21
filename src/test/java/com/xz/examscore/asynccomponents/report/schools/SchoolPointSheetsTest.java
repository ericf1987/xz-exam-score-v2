package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/5.
 */
public class SchoolPointSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolPointReport schoolPointReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolPointReport.generate("430200-9583fddde42d42b2b480b1c5c8cdaf82",
                Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52"),
                "target/school-point.xlsx");
    }
}