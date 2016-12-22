package com.xz.examscore.services;

import com.hyd.appserver.utils.StringUtils;
import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.bean.ProjectConfig;
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

    @Autowired
    ProjectConfigService projectConfigService;

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
        String cacheKey = "entry_level_student:" + projectId + ":" + range
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
     * 获取本科生排名分段
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

            //如果未统计出本科生学生录取数据，则分段参数从考试配置表中取得
            if (totalTopStudentCount == 0) {
                ProjectConfig projectConfig = new ProjectConfig(projectId);
                totalTopStudentCount = projectConfig.getRankSegmentCount();
            }
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

    public List<Double> getEntryLevelScoreLine(String projectId, Range range, Target projectTarget, int studentCount) {
        return projectConfigService.getEntryLevelScoreLine(projectId, range, projectTarget, studentCount);
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
            Document query = query(projectId, range, target);

            if (!StringUtils.isBlank(key)) {
                query.append("college_entry_level.level", key);
            }

            return (int) collection.count(query);
        });
    }

    /**
     * 查询项目的本科批次
     *
     * @param projectId 项目ID
     * @return 本科批次
     */
    public List<String> getEntryLevelKey(String projectId) {
        String cacheKey = "entry_level_key:" + projectId;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level");
            List<String> list = new ArrayList<>();
            Document query = query(projectId, null, null);
            for (String next : collection.distinct("college_entry_level.level", query, String.class)) {
                list.add(next);
            }
            return CollectionUtils.asArrayList(list);
        });
    }

    /**
     * 查询项目的本科批次
     *
     * @param projectId 项目ID
     * @return 本科批次
     */
    public List<Document> getEntryLevelDoc(String projectId) {
        String cacheKey = "entry_level_doc:" + projectId;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level");
            List<Document> list = new ArrayList<>();
            Document query = query(projectId, null, null);
            for (Document next : collection.distinct("college_entry_level", query, Document.class)) {
                list.add(next);
            }
            return CollectionUtils.asArrayList(list);
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
        String cacheKey = "entry_level_student_by_key:" + projectId + ":" + range + ":" + target + ":" + key;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("college_entry_level");
            Document query = query(projectId, range, target);

            if (!StringUtils.isBlank(key)) {
                query.append("college_entry_level.level", key);
            }

            Document projection = doc("totalScore", 1).append("rank", 1).append("student", 1).append("dValue", 1).append("_id", 0);  // 查询结果包含属性
            return CollectionUtils.asArrayList(toList(collection.find(query).projection(projection)));
        });
    }

    public String getEntryKeyDesc(String key) {
        switch (key) {
            case "ONE":
                return "A等";
            case "TWO":
                return "B等";
            case "THREE":
                return "C等";
        }
        return "";
    }
}

