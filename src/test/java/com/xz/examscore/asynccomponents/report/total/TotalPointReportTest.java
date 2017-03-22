package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/3/22.
 */
public class TotalPointReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalPointReport totalPointReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        totalPointReport.generate("430200-9583fddde42d42b2b480b1c5c8cdaf82",
                null,
                "target/total-points.xlsx");
    }
}