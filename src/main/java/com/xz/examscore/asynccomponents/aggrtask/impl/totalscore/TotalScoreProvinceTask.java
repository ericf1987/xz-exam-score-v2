package com.xz.examscore.asynccomponents.aggrtask.impl.totalscore;

import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.RangeService;
import com.xz.examscore.services.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AggrTaskMeta(taskType = "total_score_province")
public class TotalScoreProvinceTask extends AggrTask {

    @Autowired
    private RangeService rangeService;

    @Autowired
    private MongoDatabase scoreDatabase;

    @Autowired
    private ScoreService scoreService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        TotalScoreClassTask.aggrFromTotalScore(
                taskInfo, scoreDatabase, scoreService, rangeService, Range.SCHOOL, Range.PROVINCE);
    }
}
