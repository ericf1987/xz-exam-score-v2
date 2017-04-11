package com.xz.examscore.asynccomponents.report.customization;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/4/11.
 */
public class StudentProjectRankAndScoreReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentProjectRankAndScoreReport studentProjectRankAndScoreReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        String projectId = "430300-29c4d40d93bf41a5a82baffe7e714dd9";
        studentProjectRankAndScoreReport.generate(projectId, null, "target/student-project-rank-and-score.xlsx");
    }
}