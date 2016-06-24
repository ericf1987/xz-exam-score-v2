package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
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
        totalBasicDataReport.generate("431100-903288f61a5547f1a08a7e20420c4e9e", Range.province("430000"), "target/total_basic_data.xlsx");
    }
}