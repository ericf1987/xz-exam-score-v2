package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.report.schools.SchoolPointAbilityLevelSheets;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/4.
 */
public class TotalPointAbilityLevelSheetTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalPointAbilityLevelReport totalPointAbilityLevelReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalPointAbilityLevelReport.generate("430200-b73f03af1d74484f84f1aa93f583caaa", Range.province("430000"), "target/total-point-ability-level.xlsx");
    }
}