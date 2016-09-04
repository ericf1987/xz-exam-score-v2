package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ProjectConfigService;
import com.xz.examscore.services.RankLevelService;
import com.xz.examscore.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.query;

@AggrTaskMeta(taskType = "rank_level_map")
@Component
public class RankLevelMapTask extends AggrTask {

    @Autowired
    StudentService studentService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();

        // 删除旧记录
        MongoCollection<Document> rankLevelMapCollection = scoreDatabase.getCollection("rank_level_map");
        Document key = query(projectId, range, target);
        rankLevelMapCollection.deleteMany(key);

        // 保存新记录
        List<Document> rankLevelMap = generateRankLevelMap(projectId, range, target);
        rankLevelMapCollection.insertOne(doc(key).append("rankLevelMap", rankLevelMap).append("md5", MD5.digest(UUID.randomUUID().toString())));
    }

    private List<Document> generateRankLevelMap(String projectId, Range range, Target target) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

        // List{rankLevel -> count}
        List<Document> rankLevelMap = new ArrayList<>();
        List<String> studentIds = studentService.getStudentIds(projectId, range, target);
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
