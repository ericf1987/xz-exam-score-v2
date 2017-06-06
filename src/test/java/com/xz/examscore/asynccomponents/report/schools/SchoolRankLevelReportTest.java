package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/6/6.
 */
public class SchoolRankLevelReportTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    SchoolRankLevelReport schoolRankLevelReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        schoolRankLevelReport.generate("430200-937b133b4aab49b48397e5ce82b7fd0c", Range.school("a099ea83-989b-423a-8115-d6c69380a7c1"), "target/学校等第报表.xlsx");
    }
}