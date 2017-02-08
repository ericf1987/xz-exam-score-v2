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
 * @author by fengye on 2017/2/8.
 */
public class AverageTaskDispatcherTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageTaskDispatcher averageTaskDispatcher;

    @Autowired
    RangeService rangeService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Test
    public void testDispatch() throws Exception {
        String projectId = "FAKE_PROJ_1486524671547_0";
        Map<String, List<Range>> rangesMap = rangeService.getRangesMap(projectId);
        averageTaskDispatcher.dispatch(projectId, "74444", projectConfigService.getProjectConfig(projectId),rangesMap);
    }
}