package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.target2Doc;

/**
 * (description)
 * created at 16/05/21
 *
 * @author yiding_he
 */
@Service
public class RankLevelService {

    static final Logger LOG = LoggerFactory.getLogger(RankLevelService.class);

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    MongoDatabase scoreDatabase;

    public String getRankLevel(
            String projectId, String studentId, Target target, String rankRange, String defaultValue) {

        String collectionName = "rank_level";

        Document query = doc("project", projectId)
                .append("student", studentId)
                .append("target", target2Doc(target));

        Document document = scoreDatabase.getCollection(collectionName)
                .find(query).projection(doc("rankLevel", 1)).first();

        if (document != null) {
            return ((Document) document.get("rankLevel")).getString(rankRange);
        } else {
            LOG.warn("找不到排名等级（可能缺考）: query=" + query);
            return defaultValue;
        }
    }

}
