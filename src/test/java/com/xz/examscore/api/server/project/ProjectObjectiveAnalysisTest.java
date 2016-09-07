package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.mongo.QuestNoComparator;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/7.
 */
public class ProjectObjectiveAnalysisTest extends XzExamScoreV2ApplicationTests{
    private static Logger LOG = LoggerFactory.getLogger(ProjectObjectiveAnalysis.class);

    public static final Comparator<Document> QUEST_NO_COMPARATOR = new QuestNoComparator();

    @Autowired
    SchoolService schoolService;

    @Autowired
    RangeService rangeService;

    @Autowired
    QuestService questService;

    @Autowired
    QuestDeviationService questDeviationService;

    @Autowired
    OptionMapService optionMapService;

    @Autowired
    ProjectObjectiveAnalysis projectObjectiveAnalysis;

    @Test
    public void testExecute() throws Exception {

    }

    @Test
    public void testGetObjectiveAnalysis() throws Exception {
        String projectId = "433100-4cf7b0ef86574a1598481ba3e3841e42";
        String subjectId = "001";
        Range range = Range.school("64a1c8cd-a9b9-4755-a973-e1ce07f3f70a");
        projectObjectiveAnalysis.getObjectiveAnalysis(projectId, subjectId, range, questService, optionMapService, questDeviationService);
    }
}