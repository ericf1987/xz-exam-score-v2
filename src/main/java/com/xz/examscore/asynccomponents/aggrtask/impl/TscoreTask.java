package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskInfo;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@AggrTaskMeta(taskType = "t_score")
@Component
public class TscoreTask extends AggrTask {

    @Autowired
    RangeService rangeService;

    @Autowired
    AverageService averageService;

    @Autowired
    StdDeviationService stdDeviationService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    TScoreService tScoreService;

    @Autowired
    ProvinceService provinceService;

    @Override
    protected void runTask(AggrTaskInfo taskInfo) {
        String projectId = taskInfo.getProjectId();
        Target target = taskInfo.getTarget();
        Range range = taskInfo.getRange();

        Range province = Range.province(provinceService.getProjectProvince(projectId));
        double provinceAverage = averageService.getAverage(projectId, province, target);
        double provinceStdDeviation = stdDeviationService.getStdDeviation(projectId, province, target);

        double targetAverage = averageService.getAverage(projectId, range, target);
        double tscore = (targetAverage - provinceAverage) / provinceStdDeviation * 10 + 50;

        tScoreService.saveTScore(projectId, target, range, tscore);
    }
}
