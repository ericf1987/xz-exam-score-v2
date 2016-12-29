package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ProjectConfigService;
import com.xz.examscore.services.RangeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/12/29.
 */
public class StudentCompetitiveDispatcherTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentCompetitiveDispatcher studentCompetitiveDispatcher;

    @Autowired
    RangeService rangeService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Test
    public void testDispatch() throws Exception {
        String projectId = "430200-3e67c524f149491597279ef6ae31baef";
        Map<String, List<Range>> rangesMap = rangeService.getRangesMap(projectId);
        studentCompetitiveDispatcher.dispatch(projectId, "74444", projectConfigService.getProjectConfig(projectId),rangesMap);
    }
}