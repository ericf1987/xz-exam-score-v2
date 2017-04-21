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
        String projectId = "430600-12b3be890aa840c58cccdfd48b1c8a8f";
        String schoolId = "a8e50b61-547a-485d-87b1-9a1527993b80";
        studentQuestScoreRateReport.generate(projectId, Range.school(schoolId), "target/平江县第一中学-小题得分率.xlsx");
    }
}