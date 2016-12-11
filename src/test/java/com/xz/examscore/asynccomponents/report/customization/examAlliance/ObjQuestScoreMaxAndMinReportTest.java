package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/12/4.
 */
public class ObjQuestScoreMaxAndMinReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ObjQuestScoreMaxAndMinReport objQuestScoreMaxAndMinReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        objQuestScoreMaxAndMinReport.generate("430100-6da6fefff9b74e67917950567b368910", null, "target/客观题突出情况.xlsx");
    }
}