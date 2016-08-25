package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/27.
 */
public class SchoolTopStudentQuestTypeSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolTopStudentQuestTypeReport schoolTopStudentQuestTypeReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolTopStudentQuestTypeReport.generate("431100-903288f61a5547f1a08a7e20420c4e9e", Range.school("b49b8e85-f390-4e09-a709-8ab1175b0c68"), "target/school-top-student-quest-type.xlsx");
    }
}