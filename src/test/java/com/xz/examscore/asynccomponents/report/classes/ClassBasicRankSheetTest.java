package com.xz.examscore.asynccomponents.report.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/1.
 */
public class ClassBasicRankSheetTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassBasicRankReport classBasicRankReport;

    @Test
    public void testExecute() throws Exception {
        classBasicRankReport.generate("430200-3e67c524f149491597279ef6ae31baef", Range.clazz("a3fec3c6-0e46-40c3-8632-69bdf78d8484"), "target/class-score-rank.xlsx");
    }
}