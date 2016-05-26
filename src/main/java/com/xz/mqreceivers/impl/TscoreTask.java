package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.AverageService;
import com.xz.services.RangeService;
import com.xz.services.StdDeviationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;
import static com.xz.util.Mongo.query;

@ReceiverInfo(taskType = "t_score")
@Component
public class TscoreTask extends Receiver {

    @Autowired
    RangeService rangeService;

    @Autowired
    AverageService averageService;

    @Autowired
    StdDeviationService stdDeviationService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Target target = aggrTask.getTarget();
        Range range = aggrTask.getRange();

        Range province = rangeService.queryRanges(projectId, Range.PROVINCE).get(0);
        double provinceAverage = averageService.getAverage(projectId, province, target);
        double provinceStdDeviation = stdDeviationService.getStdDeviation(projectId, province, target);

        double targetAverage = averageService.getAverage(projectId, range, target);
        double tscore = (targetAverage - provinceAverage) / provinceStdDeviation * 10 + 50;

        scoreDatabase.getCollection("t_score").updateOne(
                query(projectId, range, target), $set("tScore", tscore), UPSERT);
    }
}
