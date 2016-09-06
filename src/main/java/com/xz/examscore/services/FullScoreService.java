package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 查询项目/科目/题目的满分值
 *
 * @author yiding_he
 */
@Service
public class FullScoreService {

    static final Logger LOG = LoggerFactory.getLogger(FullScoreService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询满分。其中题目满分在 quest_list，其他满分在 full_score
     *
     * @param projectId 项目ID
     * @param target    目标
     *
     * @return 目标的满分
     */
    public double getFullScore(String projectId, Target target) {
        if (target.match(Target.QUEST)) {
            return getQuestFullScore(projectId, target);
        } else {
            return getNonQuestFullScore(projectId, target);
        }
    }

    private double getNonQuestFullScore(String projectId, Target target) {
        String cacheKey = "fullscore:" + projectId + ":" + target;
        return cache.get(cacheKey, () -> {
            Document query = doc("project", projectId).append("target", Mongo.target2Doc(target));
            Document document = scoreDatabase.getCollection("full_score").find(query).first();

            if (document != null) {
                return document.getDouble("fullScore");
            } else {
                // LOG.warn("没有找到满分值: " + query.toJson());
                return 0d;
            }
        });
    }

    private double getQuestFullScore(String projectId, Target target) {
        String cacheKey = "fullscore:" + projectId + ":" + target;
        String questId = target.getId().toString();

        return cache.get(cacheKey, () -> {
            Document query = doc("project", projectId).append("questId", questId);
            Document document = scoreDatabase.getCollection("quest_list").find(query).first();

            if (document != null) {
                return document.getDouble("score");
            } else {
                LOG.warn("没有找到满分值: " + query.toJson());
                return 0d;
            }
        });
    }

    //////////////////////////////////////////////////////////////

    /**
     * 保存科目或题目的满分
     *
     * @param projectId 项目ID
     * @param target    科目或题目
     * @param fullScore 满分
     */
    public void saveFullScore(String projectId, Target target, double fullScore) {

        // 1. 更新数据库
        if (target.match(Target.QUEST)) {
            String questId = target.getId().toString();
            UpdateResult result = scoreDatabase.getCollection("quest_list").updateMany(
                    doc("project", projectId).append("questId", questId),
                    $set(doc("score", fullScore))
                    );
            if(result.getMatchedCount() == 0){
                scoreDatabase.getCollection("quest_list").insertOne(
                        doc("project", projectId).append("questId", questId)
                        .append("score", fullScore).append("md5", MD5.digest(UUID.randomUUID().toString()))
                );
            }
        } else {
            UpdateResult result = scoreDatabase.getCollection("full_score").updateMany(
                    doc("project", projectId).append("target", Mongo.target2Doc(target)),
                    $set(doc("fullScore", fullScore))
                    );
            if(result.getMatchedCount() == 0){
                scoreDatabase.getCollection("full_score").insertOne(
                        doc("project", projectId).append("target", Mongo.target2Doc(target))
                        .append("fullScore", fullScore).append("md5", MD5.digest(UUID.randomUUID().toString()))
                );
            }
        }

        // 2. 删除缓存
        String cacheKey = "fullscore:" + projectId + ":" + target;
        cache.delete(cacheKey);
    }
}
