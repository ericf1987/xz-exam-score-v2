package com.xz.report.schools;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/1.
 */
public class SchoolQuestScoreDetailSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolQuestScoreDetailReport schoolQuestScoreDetailReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolQuestScoreDetailReport.generate("430200-b73f03af1d74484f84f1aa93f583caaa", Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700"), "target/school-quest-score-detail.xlsx");
    }
}