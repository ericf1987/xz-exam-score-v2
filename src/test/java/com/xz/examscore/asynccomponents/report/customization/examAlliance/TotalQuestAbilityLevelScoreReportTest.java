package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/1/5.
 */
public class TotalQuestAbilityLevelScoreReportTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    TotalQuestAbilityLevelScoreReport totalQuestAbilityLevelScoreReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        totalQuestAbilityLevelScoreReport.generate("430200-3e67c524f149491597279ef6ae31baef", null, "target/题目能力层级统计.xlsx");
    }
}