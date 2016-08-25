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
        classBasicDataReport.generate("431100-903288f61a5547f1a08a7e20420c4e9e", Range.clazz("79cbbdaf-584b-4a58-a474-870d549bedbd"), "target/class-basic-data.xlsx");
    }
}