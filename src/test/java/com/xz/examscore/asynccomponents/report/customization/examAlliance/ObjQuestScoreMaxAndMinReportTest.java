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
        objQuestScoreMaxAndMinReport.generate("430100-f00975f88b4e4881925613b2a238673f", null, "target/客观题突出情况.xlsx");
    }
}