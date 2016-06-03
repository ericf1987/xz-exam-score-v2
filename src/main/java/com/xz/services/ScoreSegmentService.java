package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.util.CollectionUtil;
import com.xz.util.DoubleUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

/**
 * 分数段相关
 *
 * @author zhaorenwu
 */
@Service
public class ScoreSegmentService {

    public static final Logger LOG = LoggerFactory.getLogger(ScoreSegmentService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    SimpleCache cache;

    /**
     * 查询完整分数段(科目10分段，项目50分段)
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return  分数段
     */
    public List<Map<String, Object>> queryFullScoreSegment(String projectId, Target target, Range range) {
        List<Map<String, Object>> scoreSegmentList = new ArrayList<>();
        List<Document> scoreSegments = getScoreSegment(projectId, range, target);
        Map<String, Document> segmentMap = CollectionUtil.toMap(
                scoreSegments, scoreSegment -> DocumentUtils.getString(scoreSegment, "segment", ""));
        int studentCount = studentService.getStudentCount(projectId, range, target);
        double fullScore = fullScoreService.getFullScore(projectId, target);
        int incValue = getScoreSegmentInc(target);

        int endScoreSegment = 0;
        while (endScoreSegment < fullScore) {
            Map<String, Object> map = new HashMap<>();

            int startScoreSegment = endScoreSegment;
            endScoreSegment += incValue;
            if (endScoreSegment > fullScore) {
                endScoreSegment = (int) fullScore;
            }
            map.put("startScoreSegment", startScoreSegment);
            map.put("endScoreSegment", endScoreSegment);
            map.put("title", startScoreSegment + "-" + endScoreSegment);

            int count = 0;
            Document scoreSegment = segmentMap.get(String.valueOf(startScoreSegment));
            if (scoreSegment != null) {
                count = scoreSegment.getInteger("count");
            }
            map.put("count", count);
            map.put("countRate", DoubleUtils.round(studentCount == 0 ? 0 : count * 1.0 / studentCount, true));
            scoreSegmentList.add(map);
        }

        return scoreSegmentList;
    }

    /**
     * 查询分数段
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return 分数段
     */
    @SuppressWarnings("unchecked")
    public List<Document> getScoreSegment(String projectId, Range range, Target target) {
        String cacheKey = "score_segment:" + projectId + ":" + range + ":" + target;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> averageCollection = scoreDatabase.getCollection("score_segment");
            Document document = averageCollection.find(
                    new Document("project", projectId)
                            .append("range", range2Doc(range))
                            .append("target", target2Doc(target))
            ).first();

            if (document != null) {
                return new ArrayList<>(document.get("scoreSegments", List.class));
            } else {
                LOG.error("找不到分数段, project={}, range={}, target={}", projectId, range, target);
                return new ArrayList<>();
            }
        });
    }

    // 获取分段增长因子
    private int getScoreSegmentInc(Target target) {
        if (target.match(Target.PROJECT)) {
            return 50;
        } else if (target.match(Target.SUBJECT)) {
            return 10;
        } else {
            throw new IllegalArgumentException("Unsupported target: " + target);
        }
    }
}
