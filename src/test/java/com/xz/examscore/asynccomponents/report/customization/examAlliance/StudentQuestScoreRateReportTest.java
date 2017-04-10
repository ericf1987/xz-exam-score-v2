package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/4/10.
 */
public class StudentQuestScoreRateReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentQuestScoreRateReport studentQuestScoreRateReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        String projectId = "430300-c582131e66b64fe38da7d0510c399ec4";
        String schoolId = "15e70531-5ac0-475d-a2da-2fc04242ac75";
        studentQuestScoreRateReport.generate(projectId, Range.school(schoolId), "target/student-quest-score-rate.xlsx");
    }
}