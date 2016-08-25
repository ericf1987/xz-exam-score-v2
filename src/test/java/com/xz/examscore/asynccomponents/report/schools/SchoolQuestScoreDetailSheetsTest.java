package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/1.
 */
public class SchoolQuestScoreDetailSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolQuestScoreDetailReport schoolQuestScoreDetailReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolQuestScoreDetailReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.school("002e02d6-c036-4780-85d4-e54e3f1fbf9f"), "target/school-quest-score-detail.xlsx");
    }
}