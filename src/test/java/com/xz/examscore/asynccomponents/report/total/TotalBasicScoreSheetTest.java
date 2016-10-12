package com.xz.examscore.asynccomponents.report.total;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/12.
 */
public class TotalBasicScoreSheetTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    TotalBasicScoreReport totalBasicScoreReport;

    @Test
    public void testGenerateSheet() throws Exception {
        totalBasicScoreReport.generate("430600-b52cda0d90ee468094873e10ee161d4a", Range.province("430000"), "target/total-basic-score.xlsx");
    }
}