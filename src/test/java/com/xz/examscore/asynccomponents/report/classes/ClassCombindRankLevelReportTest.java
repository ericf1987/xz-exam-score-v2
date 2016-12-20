package com.xz.examscore.asynccomponents.report.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/20.
 */
public class ClassCombindRankLevelReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassCombinedRankLevelReport classCombinedRankLevelReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        classCombinedRankLevelReport.generate("430600-2404b0cc131c472dbbd13085385f5ee0", Range.clazz("e86f50b4-cbe6-403c-84d1-8cc668ee0221"), "target/class-combined-rank-level.xlsx");
    }
}