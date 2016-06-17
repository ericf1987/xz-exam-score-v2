package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.server.project.ProjectObjectiveAnalysis;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/17.
 */
public class TotalPaperObjectiveSheetsTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    TotalPaperObjectiveReport totalPaperObjectiveReport;

    @Test
    public void testGetSheetTask() throws Exception{
        totalPaperObjectiveReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.province("430000"), "target/total-paper-objective.xlsx");
    }
}