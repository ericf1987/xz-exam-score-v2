package com.xz.report.schools;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/8.
 */
public class SchoolBasicScoreReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolBasicScoreReport schoolBasicScoreReport;

    @Test
    public void testGetSheetTask()throws Exception{
        schoolBasicScoreReport.generate("430200-89c9dc7481cd47a69d85af3f0808e0c4", Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700"), "target/school-basic-score.xlsx");
    }
}