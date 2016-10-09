package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/8.
 */
public class SchoolBasicSubjectReportTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SchoolBasicSubjectReport schoolBasicSubjectReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        schoolBasicSubjectReport.generate("430600-b52cda0d90ee468094873e10ee161d4a", Range.school("15c60ef9-b97c-48fb-bfbe-4c2c24bbea33"), "target/school-basic-subject.xlsx");
    }
}