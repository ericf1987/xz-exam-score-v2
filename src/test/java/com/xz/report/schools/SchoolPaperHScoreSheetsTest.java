package com.xz.report.schools;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/25.
 */
public class SchoolPaperHScoreSheetsTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    SchoolPaperHScoreReport schoolPaperHScoreReport;

    @Test
    public void testGenerateSheet() throws Exception {
        schoolPaperHScoreReport.generate("431100-903288f61a5547f1a08a7e20420c4e9e", Range.school("b49b8e85-f390-4e09-a709-8ab1175b0c68"), "target/school-paper-high-score.xlsx");
    }
}