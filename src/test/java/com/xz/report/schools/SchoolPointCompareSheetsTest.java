package com.xz.report.schools;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/1.
 */
public class SchoolPointCompareSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    SchoolPointCompareReport schoolPointCompareReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolPointCompareReport.generate("430100-8d805ef37b2f4bc7ad9808a9a109dc22", Range.school("02784aa8-c523-497e-8536-7cd3c23f1126"), "target/school-point-compare.xlsx");
    }
}