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
        String projectId = "430100-dd3013ab961946fb8a3668e5ccc475b6";
        String schoolId = "d9bdecc9-0185-4688-90d1-1aaf27e2dcfd";
        studentQuestScoreRateReport.generate(projectId, Range.school(schoolId), "target/长沙县第一中学-小题得分率.xlsx");
    }
}