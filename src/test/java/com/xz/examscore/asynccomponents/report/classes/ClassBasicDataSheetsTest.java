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
        classBasicDataReport.generate("430200-cc721d3beb924d2997fe112c767b3a28", Range.clazz("fc512df4-fb9c-4e38-a2ce-5835e7e55a78"), "target/class-basic-data.xlsx");
    }
}