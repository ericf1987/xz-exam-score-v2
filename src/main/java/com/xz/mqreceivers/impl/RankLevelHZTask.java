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

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        //1.获取必要信息
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();//班级或学校
        Target target = aggrTask.getTarget();//考试科目

        //获取学生班级和学校信息

        //获取考试科目信息

        MongoCollection<Document> rankLevelCountCollection =
                scoreDatabase.getCollection("score_rank_level_map");
        Document id = Mongo.generateId(projectId, range, target);
    }
}
