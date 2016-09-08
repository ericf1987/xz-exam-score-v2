package com.xz.examscore.asynccomponents.report.classes.compare;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/26.
 */
public class ClassScoreCompareSheetsTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassScoreCompareReport classScoreCompareReport;

    @Test
    public void testGenerateSheet() throws Exception {
        classScoreCompareReport.generate("433100-fef19389d6ce4b1f99847ab96d2cfeba", Range.clazz("f8259b31-7c8b-47ba-90d5-c5c15763660f"), "target/class-score-compare.xlsx");
    }
}