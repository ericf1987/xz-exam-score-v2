package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/19.
 */
public class SchoolTopStudentSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolTopStudentReport schoolTopStudentReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolTopStudentReport.generate("430100-2df3f3ad199042c39c5f4b69f5dc7840", Range.school("d00faaa0-8a9b-45c4-ae16-ea2688353cd0"), "target/school-top-student.xlsx");
    }
}