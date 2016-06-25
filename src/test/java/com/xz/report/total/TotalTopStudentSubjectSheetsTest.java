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
        totalTopStudentSubjectReport.generate("431100-903288f61a5547f1a08a7e20420c4e9e", Range.province("430000"), "target/total-top-student-subjective.xlsx");
    }
}