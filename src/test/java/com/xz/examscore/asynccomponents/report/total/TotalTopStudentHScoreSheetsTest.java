package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/17.
 */
public class TotalTopStudentHScoreSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalTopStudentHScoreReport totalTopStudentHScoreReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalTopStudentHScoreReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total-top-student.xlsx");
    }
}