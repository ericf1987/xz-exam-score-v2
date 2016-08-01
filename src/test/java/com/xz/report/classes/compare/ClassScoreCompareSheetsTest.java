package com.xz.report.classes.compare;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.server.classes.compare.ClassScoreCompareAnalysis;
import com.xz.bean.Range;
import com.xz.report.classes.ClassQuestScoreDetailReport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/26.
 */
public class ClassScoreCompareSheetsTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassScoreCompareReport classScoreCompareReport;

    @Test
    public void testGenerateSheet() throws Exception {
        classScoreCompareReport.generate("430200-b73f03af1d74484f84f1aa93f583caaa", Range.clazz("0c738247-b62c-4c90-9016-1cc1163fd0b1"), "target/class-score-compare.xlsx");
    }
}