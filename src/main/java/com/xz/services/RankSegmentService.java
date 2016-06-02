package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 查询排名分段比率
 *
 * @author yiding_he
 */
@Service
public class RankSegmentService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询完整的排行分段
     *
     * @param projectId 考试项目id
     * @param target    目标
     * @param range     范围
     *
     * @return  完整的排行分段
     */
    public List<Map<String, Object>> queryFullRankSegment(String projectId, Target target, Range range) {
        List<Document> rankSegments = getRankSegments(projectId, range, target);
        rankSegments.sort((o1, o2) -> o1.getDouble("rankPercent").compareTo(o2.getDouble("rankPercent")));

        double totalRate = 0;
        int minPercent, maxPercent= 0;
        List<Map<String, Object>> rankStats = new ArrayList<>();

        for (Document rankSegment : rankSegments) {
            Map<String, Object> map = new HashMap<>();

            double rankPercent = rankSegment.getDouble("rankPercent");
            double rate = rankSegment.getDouble("rate");
            int count = rankSegment.getInteger("count");
            minPercent = maxPercent;
            maxPercent = (int) (rankPercent * 100);
            totalRate += rate;
            String title = minPercent + "-" + maxPercent;

            map.put("minPercent", minPercent);
            map.put("maxPercent", maxPercent);
            map.put("count", count);
            map.put("rate", rate);
            map.put("rankPercent", rankPercent);
            map.put("totalRate", totalRate);
            map.put("title", title);

            rankStats.add(map);
        }

        return rankStats;
    }

    @SuppressWarnings("unchecked")
    public List<Document> getRankSegments(String projectId, Range range, Target target) {

        Document query = Mongo.query(projectId, range, target);
        Document doc = scoreDatabase.getCollection("rank_segment").find(query).first();

        if (doc != null) {
            return (List<Document>) doc.get("rankSegments");
        } else {
            return Collections.emptyList();
        }
    }
}
