package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/24.
 */
public class TotalBasicDataSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalBasicDataReport totalBasicDataReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalBasicDataReport.generate("430200-cc721d3beb924d2997fe112c767b3a28", Range.province("430000"), "target/total_basic_data.xlsx");
    }
}