package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.CollegeEntryLevelService;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.RangeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/24.
 */
public class CollegeEntryLevelTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    CollegeEntryLevelTask task;

    @Autowired
    RangeService rangeService;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430100-1944e9f7048b48e2b38e35db75be4980";
        String type = "college_entry_level";
        Range schoolRange = Range.school("d00faaa0-8a9b-45c4-ae16-ea2688353cd0");
        Range clazzRange = Range.clazz("de0bbb89-ef61-488f-b639-2a532030946a");
        Range provinceRange = rangeService.queryProvinceRange(projectId);

        task.runTask(new AggrTaskMessage(projectId, "aaa", type)
                .setRange(clazzRange)
                .setTarget(Target.project(projectId)));
    }
}