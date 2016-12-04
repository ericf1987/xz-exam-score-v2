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
        objQuestScoreMaxAndMinReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", null, "target/客观题突出情况.xlsx");
    }
}