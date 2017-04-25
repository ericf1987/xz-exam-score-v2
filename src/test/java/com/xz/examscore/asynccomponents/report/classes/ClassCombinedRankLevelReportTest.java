package com.xz.examscore.asynccomponents.report.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.server.classes.ClassCombinedRankLevelAnalysis;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/4/25.
 */
public class ClassCombinedRankLevelReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassCombinedRankLevelReport classCombinedRankLevelReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        classCombinedRankLevelReport.generate("430300-32d8433951ce43cab5883abff77c8ea3",
                Range.clazz("f33ab424-ec1e-4f55-a45d-47ad84faa796"), "target/class-combined-rank-level1.xlsx");
    }
}