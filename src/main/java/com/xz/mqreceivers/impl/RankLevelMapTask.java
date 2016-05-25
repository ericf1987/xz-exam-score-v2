package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ProjectConfigService;
import com.xz.services.RankLevelService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.generateId;

@ReceiverInfo(taskType = "rank_level_map")
@Component
public class RankLevelMapTask extends Receiver {

    @Autowired
    StudentService studentService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        // 删除旧记录
        MongoCollection<Document> rankLevelMapCollection = scoreDatabase.getCollection("rank_level_map");
        Document key = generateId(projectId, range, target);
        rankLevelMapCollection.deleteOne(key);

        // 保存新记录
        List<Document> rankLevelMap = generateRankLevelMap(projectId, range, target);
        rankLevelMapCollection.insertOne(doc(key).append("rankLevelMap", rankLevelMap));
    }

    private List<Document> generateRankLevelMap(String projectId, Range range, Target target) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

        // List{rankLevel -> count}
        List<Document> rankLevelMap = new ArrayList<>();
        List<String> studentIds = studentService.getStudentList(projectId, range, target);
        String lastRankLevel = projectConfig.getLastRankLevel();

        for (String studentId : studentIds) {
            String rankLevel = rankLevelService.getRankLevel(
                    projectId, studentId, target, range.getName(), lastRankLevel);

            addUpRankLevel(rankLevelMap, rankLevel);
        }
        return rankLevelMap;
    }

    private void addUpRankLevel(List<Document> rankLevelMap, String rankLevel) {

        for (Document document : rankLevelMap) {
            if (document.getString("rankLevel").equals(rankLevel)) {
                document.put("count", document.getInteger("count") + 1);
                return;
            }
        }

        // 没有找到对应的 Document
        rankLevelMap.add(doc("rankLevel", rankLevel).append("count", 1));
    }
}
