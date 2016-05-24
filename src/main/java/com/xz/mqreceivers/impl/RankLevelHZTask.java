package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.RankLevelCountService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by fengye on 2016/5/21.
 */
@ReceiverInfo(taskType="ranking_level_hz")
@Component
public class RankLevelHZTask extends Receiver{
    @Autowired
    RankLevelCountService rankLevelCountService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        //1.获取考试项目编号
        String projectId = aggrTask.getProjectId();
        //执行等第统计
        rankLevelCountService.generateRankLevelHZ(projectId);
    }
}
