package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Target;
import com.xz.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

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
            return getQuestFullScore(projectId, target.getId().toString());
        } else {
            return getSubjectProjectFullScore(projectId, target);
        }
    }

    private double getSubjectProjectFullScore(String projectId, Target target) {
        Document query = doc("project", projectId).append("target", Mongo.target2Doc(target));
        Document document = scoreDatabase.getCollection("full_score").find(query).first();

        if (document != null) {
            return document.getDouble("fullScore");
        } else {
            // LOG.warn("没有找到满分值: " + query.toJson());
            return 0;
        }
    }

    private double getQuestFullScore(String projectId, String questId) {
        Document query = doc("project", projectId).append("questId", questId);
        Document document = scoreDatabase.getCollection("quest_list").find(query).first();

        if (document != null) {
            return document.getDouble("score");
        } else {
            LOG.warn("没有找到满分值: " + query.toJson());
            return 0;
        }
    }
}
