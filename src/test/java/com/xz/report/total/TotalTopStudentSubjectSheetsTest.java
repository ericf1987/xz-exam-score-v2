package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/19.
 */
public class TotalTopStudentSubjectSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalTopStudentSubjectReport totalTopStudentSubjectReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalTopStudentSubjectReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total-top-student-subjective.xlsx");
    }
}