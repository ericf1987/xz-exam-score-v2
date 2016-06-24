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
        schoolBasicScoreSegmentReport.generate("431100-903288f61a5547f1a08a7e20420c4e9e", Range.school("091c8d3b-9f84-4cf4-a4fa-7bc8029ff693"), "target/school-basic-score-segment.xlsx");
    }
}