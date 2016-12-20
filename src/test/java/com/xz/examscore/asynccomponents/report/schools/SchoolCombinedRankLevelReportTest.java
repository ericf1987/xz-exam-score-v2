package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/12/20.
 */
public class SchoolCombinedRankLevelReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolCombinedRankLevelReport schoolCombinedRankLevelReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        schoolCombinedRankLevelReport.generate("430600-2404b0cc131c472dbbd13085385f5ee0", Range.school("d1bf6d54-1e2e-40b3-b3df-fda8069e4389"), "target/school-combined-rank-level.xlsx");

    }
}