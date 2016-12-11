package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/4.
 */
public class QuestScoreDetailReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestScoreDetailReport questScoreDetailReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        questScoreDetailReport.generate("430100-6da6fefff9b74e67917950567b368910", null, "target/试题得分明细.xlsx");
    }
}