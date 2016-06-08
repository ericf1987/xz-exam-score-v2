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
        schoolBasicScoreReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.school("002e02d6-c036-4780-85d4-e54e3f1fbf9f"), "target/school-basic-score.xlsx");
    }
}