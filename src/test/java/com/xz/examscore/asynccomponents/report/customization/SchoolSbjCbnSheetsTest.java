package com.xz.examscore.asynccomponents.report.customization;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/20.
 */
public class SchoolSbjCbnSheetsTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolSbjCbnReport schoolSbjCbnReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolSbjCbnReport.generate("430100-2c641a3e36ff492aa535da7fb4cf28cf", null, "target/school-sbj-cbn-compare.xlsx");
    }
}