package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;
import static com.xz.examscore.util.Mongo.query;

/**
 * @author by fengye on 2016/10/24.
 */
@Service
public class CollegeEntryLevelService {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询本科录取学生列表
     *
     * @param projectId 项目ID
     * @param range     排名范围
     * @param target    排名目标
     * @param minRank   最小名次
     * @param maxRank   最大名次
     * @return 本科录取学生列表
     */
    public List<Document> getEntryLevelStudent(String projectId, Range range, Target target, int minRank, int maxRank) {
        String cacheKey = "college_entry_level:" + projectId + ":" + range
                + ":" + target + ":" + minRank + ":" + maxRank;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level");
            Document query = query(projectId, range, target)
                    .append("rank", doc("$gte", minRank).append("$lte", maxRank));

            Document projection = doc("totalScore", 1).append("rank", 1).append("student", 1).append("college_entry_level", 1).append("dValue", 1);  // 查询结果包含属性

            return new ArrayList<>(MongoUtils.toList(collection.find(query).projection(projection)));
        });
    }

    /**
     * 获取本科录取学生最大排名
     *
     * @param projectId 考试项目id
     * @param range     范围
     * @return 本科录取学生最大名次
     */
    private int getEntryLevelStudentMaxRank(String projectId, Range range) {
        String cacheKey = "entry_level_student_max_rank:" + projectId + ":" + range;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level");
            Document document = collection.find(query(projectId, range)).sort(doc("rank", -1)).first();

            if (document != null) {
                return document.getInteger("rank");
            }

            return 0;
        });
    }

    /**
     * 获取尖子生排名分段
     *
     * @param projectId 考试项目id
     * @param range     范围
     * @return 排名分段
     */
    public List<Map<String, Object>> getEntryLevelStudentRankSegment(String projectId, Range range) {

        String cacheKey = "entry_level_student_rank_segment:" + projectId + ":" + range;

        return cache.get(cacheKey, () -> {
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            int totalTopStudentCount = getEntryLevelStudentMaxRank(projectId, range);
            int startIndex = 1;
            int endIndex = 0;

            while (endIndex < totalTopStudentCount) {
                Map<String, Object> rankSegment = new HashMap<>();

                startIndex = endIndex + 1;
                endIndex += 40;
                if (endIndex > totalTopStudentCount) {
                    endIndex = totalTopStudentCount;
                }

                rankSegment.put("startIndex", startIndex);
                rankSegment.put("endIndex", endIndex);
                rankSegment.put("title", startIndex + "-" + endIndex);

                list.add(rankSegment);
            }

            return list;
        });
    }

    public Map<String, Double> getEntryLevel(String projectId) {
        Map<String, Double> map = new HashMap<>();
        map.put("ONE", 400d);
        map.put("TWO", 300d);
        map.put("THREE", 200d);
        return map;
    }

    public String[] getEntryLevelKey(String projectId) {
        return new String[]{"ONE", "TWO", "THREE"};
    }

    /**
     * 查询上线率学生人数
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param key       上线率参数
     * @return 上线率人数
     */
    public int getEntryLevelStudentCount(String projectId, Range range, Target target, String key) {
        String cacheKey = "entry_level_student_count:" + projectId + ":" + range + ":" + target + ":" + key;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level");
            Document query = query(projectId, range, target)
                    .append("college_entry_level.level", key);

            return (int) collection.count(query);
        });
    }

    /**
     * 查询上线率学生人数
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param key       上线率参数
     * @return 上线率人数
     */
    public ArrayList<Document> getEntryLevelStudentByKey(String projectId, Range range, Target target, String key) {
        String cacheKey = "entry_level_student:" + projectId + ":" + range + ":" + target + ":" + key;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level");
            Document query = query(projectId, range, target)
                    .append("college_entry_level.level", key);
            Document projection = doc("totalScore", 1).append("rank", 1).append("student", 1).append("dValue", 1).append("_id", 0);  // 查询结果包含属性
            return CollectionUtils.asArrayList(toList(collection.find(query).projection(projection)));
        });
    }

    public String getEntryKeyDesc(String key) {
        switch (key) {
            case "ONE":
                return "一本上线率";
            case "TWO":
                return "二本上线率";
            case "THREE":
                return "三本上线率";
        }
        return "";
    }
}

