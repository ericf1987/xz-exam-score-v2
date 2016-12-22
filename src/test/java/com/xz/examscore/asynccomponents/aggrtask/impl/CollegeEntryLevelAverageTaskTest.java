package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ProvinceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/12/22.
 */
public class CollegeEntryLevelAverageTaskTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    CollegeEntryLevelAverageTask collegeEntryLevelAverageTask;

    @Autowired
    ProvinceService provinceService;

    @Test
    public void testRunTask() throws Exception {
        AggrTaskMessage atm = new AggrTaskMessage();
        String projectId = "430500-ea90a33d908c40aba5907bd97b838d61";
        atm.setProjectId(projectId);
        String province = provinceService.getProjectProvince(projectId);
        Range provinceRange = Range.province(province);
        atm.setRange(provinceRange);
        atm.setTarget(Target.project(projectId));
        atm.setType("college_entry_level_average");
        atm.setAggregationId("67978896786");
        collegeEntryLevelAverageTask.runTask(atm);
    }
}