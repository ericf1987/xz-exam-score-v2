package com.xz.report.schools;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/8.
 */
public class SchoolBasicScoreSegmentReportTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SchoolBasicScoreSegmentReport schoolBasicScoreSegmentReport;

    @Test
    public void testGetSheetTask() throws Exception{
        schoolBasicScoreSegmentReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47"), "target/school-basic-score-segment.xlsx");
    }
}