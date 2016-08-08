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
        classRankLevelReport.generate("430100-e7bd093d92d844819c7eda8b641ab6ee", Range.clazz("048eb56f-a105-4992-8228-0e436c9e4670"), "target/class-rank-level.xlsx");
    }
}