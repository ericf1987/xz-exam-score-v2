package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author by fengye on 2016/12/3.
 */
public class TotalScoreSegmentCountAnalysisTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    TotalScoreSegmentCountAnalysis totalScoreSegmentCountAnalysis;

    @Test
    public void testExecute() throws Exception {
        TotalScoreSegmentCountAnalysis.ScoreSegmentAnalyzer analyzer = totalScoreSegmentCountAnalysis.new ScoreSegmentAnalyzer(900, 300, 100);
        List<String> spans = analyzer.getSpans(analyzer.getMax(), analyzer.getMin(), analyzer.getStepValue());
        System.out.println(spans.toString());
    }
}