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
        sbjQuestScoreMaxAndMinReport.generate("430100-f00975f88b4e4881925613b2a238673f", null, "target/主观题突出情况.xlsx");
    }
}