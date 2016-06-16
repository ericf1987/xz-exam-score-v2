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
        totalBasicSubjectReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total-basic-subject.xlsx");
/*        double tscore = (58.12d - 63.63d) / 104.93d * 10 + 50;
        System.out.println(tscore);*/
    }
}
