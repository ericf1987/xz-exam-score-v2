package com.xz.report.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.ScoreService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/1.
 */
public class ClassQuestScoreDetailSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    ClassQuestScoreDetailReport classQuestScoreDetailReport;
    @Autowired
    ScoreService scoreService;

    @Test
    public void testGenerateSheet() throws Exception {
        classQuestScoreDetailReport.generate("430200-b73f03af1d74484f84f1aa93f583caaa", Range.clazz("0c738247-b62c-4c90-9016-1cc1163fd0b1"), "target/class-quest-score-detail.xlsx");
/*        double score = scoreService.getScore("430200-b73f03af1d74484f84f1aa93f583caaa", Range.student("070bf582-7e0e-4b8e-8bda-afda7d09a17a"), Target.quest("577261da2d560287556e76e3"));
        System.out.println(score);*/
    }
}