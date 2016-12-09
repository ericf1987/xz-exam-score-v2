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
        questScoreDetailReport.generate("430100-f00975f88b4e4881925613b2a238673f", null, "target/试题得分明细.xlsx");
    }
}