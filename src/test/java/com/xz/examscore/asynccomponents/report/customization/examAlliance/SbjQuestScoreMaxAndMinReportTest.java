package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/12/4.
 */
public class SbjQuestScoreMaxAndMinReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SbjQuestScoreMaxAndMinReport sbjQuestScoreMaxAndMinReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        sbjQuestScoreMaxAndMinReport.generate("430100-6da6fefff9b74e67917950567b368910", null, "target/主观题突出情况.xlsx");
    }
}