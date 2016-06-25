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
        totalPaperObjectiveReport.generate("431100-903288f61a5547f1a08a7e20420c4e9e", Range.province("430000"), "target/total-paper-objective.xlsx");
    }
}