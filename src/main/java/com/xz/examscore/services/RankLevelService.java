package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * (description)
 * created at 16/05/21
 *
 * @author yiding_he
 */
@SuppressWarnings("unchecked")
@Service
public class RankLevelService {

    static final Logger LOG = LoggerFactory.getLogger(RankLevelService.class);

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    public String getRankLevel(
            String projectId, String studentId, Target target, String rankRange, String defaultValue) {

        String cacheKey = "rank_level:" + projectId + ":" + studentId + ":" + target + ":" + rankRange;

        return cache.get(cacheKey, () -> {
            return getRankLevel0(projectId, studentId, target, rankRange, defaultValue);
        });
    }

    private String getRankLevel0(String projectId, String studentId, Target target, String rankRange, String defaultValue) {
        String collectionName = "rank_level";

        Document query = doc("project", projectId)
                .append("student", studentId)
                .append("target", target2Doc(target));

        Document document = scoreDatabase.getCollection(collectionName)
                .find(query).projection(doc("rankLevel", 1)).first();

        if (document != null) {
            Document rankLevelDoc = (Document)document.get("rankLevel");
            if(rankLevelDoc != null && !rankLevelDoc.isEmpty()){
                return rankLevelDoc.getString(rankRange);
            }else{
                return defaultValue;
            }
        } else {
            LOG.warn("找不到排名等级（可能缺考）: query=" + query.toJson());
            return defaultValue;
        }
    }

    public List<Map<String, Object>> getRankLevelMap(String projectId, Range range, Target target) {
        String cacheKey = "rank_level_map:" + projectId + ":" + range + ":" + target;
        return cache.get(cacheKey, () -> {
            return getRankLevelMap0(projectId, range, target);
        });
    }

    public ArrayList<Map<String, Object>> getRankLevelMap0(String projectId, Range range, Target target) {
        ArrayList <Map<String, Object>> list = new ArrayList<>();
        MongoCollection<Document> collection = scoreDatabase.getCollection("rank_level_map");

        Document query = doc("project", projectId)
                .append("range", range2Doc(range))
                .append("target", target2Doc(target));

        Document document = collection.find(query).projection(doc("rankLevelMap", 1)).first();
        List<Document> rankLevelMaps = (List<Document>)document.get("rankLevelMap");
        for(Document doc : rankLevelMaps){
            String rankLevel = doc.getString("rankLevel");
            int count = doc.getInteger("count");
            Map<String, Object> map = new HashMap<>();
            map.put("rankLevel", rankLevel);
            map.put("count", count);
            list.add(map);
        }

        return list;
    }

}
