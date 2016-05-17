package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.RankService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@ReceiverInfo(taskType = "ranking_level")
@Component
public class RankingLevelTask extends Receiver {

    @Autowired
    RankService rankService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range rankRange = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        // 查询符合条件的总分记录，对每条记录查询分数的排名等级，并保存
        Document query = doc("project", projectId)
                .append("range.name", Range.STUDENT)
                .append("target", doc("name", target.getName()).append("id", target.idToParam()));

        MongoCollection<Document> collection = scoreDatabase.getCollection("total_score");
        for (Document document : collection.find(query)) {
            String studentId = ((Document) document.get("range")).getString("id");
            String rankLevel = rankService.getRankLevel(projectId, rankRange, target, studentId);

            collection.updateOne(
                    doc("_id", document.getObjectId("_id")),
                    $set("rankLevel." + rankRange.getName(), rankLevel));
        }
    }
}
