package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ProjectConfigService;
import com.xz.examscore.services.RangeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/12/22.
 */
public class CollegeEntryLevelAverageDispatcherTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    CollegeEntryLevelAverageDispatcher collegeEntryLevelAverageDispatcher;

    @Autowired
    RangeService rangeService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Test
    public void testDispatch() throws Exception {
        String projectId = "430500-ea90a33d908c40aba5907bd97b838d61";
        Map<String, List<Range>> rangesMap = rangeService.getRangesMap(projectId);
        collegeEntryLevelAverageDispatcher.dispatch(projectId, "74444", projectConfigService.getProjectConfig(projectId),rangesMap);
    }
}