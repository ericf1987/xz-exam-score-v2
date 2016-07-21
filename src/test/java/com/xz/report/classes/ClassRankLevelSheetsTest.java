package com.xz.report.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/21.
 */
public class ClassRankLevelSheetsTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassRankLevelReport classRankLevelReport;

    @Test
    public void testGenerateSheet() throws Exception {
        classRankLevelReport.generate("430200-b73f03af1d74484f84f1aa93f583caaa", Range.clazz("0c738247-b62c-4c90-9016-1cc1163fd0b1"), "target/class-rank-level.xlsx");
    }
}