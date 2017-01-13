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
        String projectId = "431100-205d7de6c73441cabb4a301bf1415b5e";
        String type = "college_entry_level";
        Range schoolRange = Range.school("80e503de-072c-4e26-845f-271e841bf47a");
        Range clazzRange = Range.clazz("7e68a09b-e11f-413c-baa3-c829541f0767");
        Range provinceRange = rangeService.queryProvinceRange(projectId);

        task.runTask(new AggrTaskMessage(projectId, "aaa", type)
                .setRange(clazzRange)
                .setTarget(Target.project(projectId)));
    }
}