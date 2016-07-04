package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/1.
 */
public class TotalQuestScoreDetailSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalQuestScoreDetailReport totalQuestScoreDetailReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalQuestScoreDetailReport.generate("430200-b73f03af1d74484f84f1aa93f583caaa", Range.province("430000"), "target/total-paper-subjective.xlsx");

    }
}