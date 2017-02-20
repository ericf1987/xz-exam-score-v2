package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/1.
 */
public class SchoolQuestScoreDetailSheetsTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SchoolQuestScoreDetailReport schoolQuestScoreDetailReport;

    @Test
    public void testGenerateSheet() throws Exception {
        long begin = System.currentTimeMillis();
        schoolQuestScoreDetailReport.generate("FAKE_PROJ_1486524671547_0",
                Range.school("SCHOOL_1486524671577_1"), "target/school-quest-score-detail-new.xlsx");
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - begin));

    }
}