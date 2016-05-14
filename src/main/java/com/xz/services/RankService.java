package com.xz.services;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
@Service
public class RankService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    /**
     * 查询排名
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param studentId 学生ID
     *
     * @return 分数在指定目标和范围内的排名
     */
    public int getRank(String projectId, Range range, Target target, String studentId) {
        double score = scoreService.getScore(projectId, Range.student(studentId), target);
        return getRank(projectId, range, target, score);
    }

    /**
     * 查询排名
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param score     分数
     *
     * @return 分数在指定目标和范围内的排名
     */
    public int getRank(String projectId, Range range, Target target, double score) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("score_rank_map");
        Document id = Mongo.generateId(projectId, range, target);

        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                $match("_id", id), $unwind("$scoreMap"), $match("scoreMap.score", $gt(score)),
                $group(doc("_id", null).append("count", $sum("$scoreMap.count")))
        ));

        return aggregate.first().getInteger("count") + 1;
    }
}
