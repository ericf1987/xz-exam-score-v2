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
        Range schoolRange = Range.school("64a1c8cd-a9b9-4755-a973-e1ce07f3f70a");
        Range clazzRange = Range.clazz("0b0e704f-955b-456c-878d-015a8a621051");
        Range provinceRange = rangeService.queryProvinceRange(projectId);

        task.runTask(new AggrTaskMessage(projectId, "aaa", type)
                .setRange(provinceRange)
                .setTarget(Target.project(projectId)));
    }
}