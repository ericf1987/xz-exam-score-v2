package com.xz.examscore.asynccomponents.report.customization;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.server.customization.ClassSbjCbnCompare;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/20.
 */
public class ClassSbjCbnSheetsTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassSbjCbnReport classSbjCbnReport;

    @Test
    public void testGenerateSheet() throws Exception {
        classSbjCbnReport.generate("430700-caa7e02622ca402eb4a2fd071580373b", null, "target/class-sbj-cbn-compare.xlsx");
    }
}