package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/8.
 */
public class TotalBasicSubjectReportTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    TotalBasicSubjectReport totalBasicSubjectReport;

    @Test
    public void testGetSheetTask() throws Exception{
        totalBasicSubjectReport.generate("430200-89c9dc7481cd47a69d85af3f0808e0c4", Range.province("430000"), "target/total-basic-subject.xlsx");
    }
}
