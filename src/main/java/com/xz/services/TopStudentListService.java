package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.query;

/**
 * (description)
 * created at 16/06/13
 *
 * @author yiding_he
 */
@Service
public class TopStudentListService {

    public static final double TOP_STUDENT_RATE = 0.05;     // 尖子生取参考总人数的前5%

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    @Autowired
    StudentService studentService;

    /**
     * 查询指定排行范围下，指定（学校/班级）尖子生所占比例
     *
     * @param projectId     考试项目id
     * @param rankRange     排行范围
     * @param compareRange  比较范围
     * @param target        目标
     *
     * @return  占比
     */
    public int getTopStudentCount(String projectId, Range rankRange, Range compareRange,
                                    Target target, int minRank, int maxRank) {
        String cacheKey = "top_student_rate:" + projectId + ":" + rankRange + ":" +
                compareRange + ":" + target + ":" + minRank + ":" + maxRank;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("top_student_list");
            Document query = query(projectId, rankRange, target)
                    .append("rank", doc("$gte", minRank).append("$lte", maxRank));

            if (compareRange.match(Range.SCHOOL)) {
                query.append("school", compareRange.getId());
            } else if (compareRange.match(Range.CLASS)) {
                query.append("class", compareRange.getId());
            } else {
                throw new IllegalArgumentException("Unsupported compareRange: " + compareRange);
            }

            return (int) collection.count(query);
        });
    }

    /**
     * 获取尖子生排名分段
     *
     * @param projectId 考试项目id
     * @param range     范围
     *
     * @return  排名分段
     */
    public List<Map<String, Object>> getTopStudentRankSegment(String projectId, Range range) {
        String cacheKey = "top_student_rank_segment:" + projectId + ":" + range;
        return cache.get(cacheKey, () -> {
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            int totalTopStudentCount = getTopStudentTotalCount(projectId, range);
            int startIndex = 1;
            int endIndex = 0;

            while (endIndex < totalTopStudentCount) {
                Map<String, Object> rankSegment = new HashMap<>();

                startIndex = endIndex + 1;
                endIndex += 50;
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

    /**
     * 获取尖子生总人数
     *
     * @param projectId 考试项目id
     * @param range     范围
     *
     * @return  尖子生人数
     */
    public int getTopStudentTotalCount(String projectId, Range range) {
        String cacheKey = "top_student_count:" + projectId + ":" + range;
        return cache.get(cacheKey, () -> {
            int studentCount = studentService.getStudentCount(projectId, range);
            return (int) ((studentCount * TOP_STUDENT_RATE) + 0.5);
        });
    }

    /**
     * 查询尖子生列表
     *
     * @param projectId 项目ID
     * @param range     排名范围
     * @param target    排名目标
     * @param minRank   最小名次
     * @param maxRank   最大名次
     *
     * @return 尖子生列表
     */
    public List<Document> getTopStudentList(
            String projectId, Range range, Target target, int minRank, int maxRank) {
        String cacheKey = "top_student_list:" + projectId + ":" + range
                + ":" + target + ":" + minRank + ":" + maxRank;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("top_student_list");
            Document query = query(projectId, range, target)
                    .append("rank", doc("$gte", minRank).append("$lte", maxRank));

            Document projection = doc("score", 1).append("rank", 1).append("student", 1);  // 查询结果包含属性

            return new ArrayList<>(MongoUtils.toList(collection.find(query).projection(projection)));
        });
    }

    /**
     * 查询尖子生中排名最后一位
     * @param projectId 项目ID
     * @param range     排名范围
     * @param target    排名目标
     */
    public Document getTopStudentLastOne(String projectId, Range range, Target target){
        String cacheKey = "top_student_last_one:" + projectId + ":" + range + ":" + target;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("top_student_list");
            return collection.find(query(projectId, range, target)).sort(doc("rank", -1)).first();
        });
    }
}
