package com.xz.examscore.asynccomponents.report.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/24.
 */
public class ClassBasicDataSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    ClassBasicDataReport classBasicDataReport;

    @Test
    public void testGenerateSheet() throws Exception {
        classBasicDataReport.generate("430100-5d2142085fc747c9b5b230203bbfd402", Range.clazz("42f66486-e24c-4ea7-bf1a-28bbd7313c72"), "target/class-basic-data.xlsx");
    }
}