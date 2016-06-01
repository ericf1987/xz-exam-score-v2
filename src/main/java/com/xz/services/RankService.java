package com.xz.services;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 查询排名和排名等级
 *
 * @author yiding_he
 */
@Service
public class RankService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    ProjectConfigService projectConfigService;

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
        MongoCollection<Document> collection = scoreDatabase.getCollection("score_map");
        Document id = Mongo.query(projectId, range, target);

        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                $match(id), $unwind("$scoreMap"), $match("scoreMap.score", $gt(score)),
                $group(doc("_id", null).append("count", $sum("$scoreMap.count")))
        ));

        Document document = aggregate.first();
        if (document == null) {
            return 1;
        }

        return document.getInteger("count") + 1;
    }

    /**
     * 查询考生分数的排名等级
     *
     * @param projectId 项目ID
     * @param range     排名范围
     * @param target    排名目标
     * @param studentId 学生ID
     *
     * @return 排名等级
     */
    public String getRankLevel(String projectId, Range range, Target target, String studentId) {
        int rank = getRank(projectId, range, target, studentId);
        int studentCount = studentService.getStudentCount(projectId, range);

        ProjectConfig config = projectConfigService.getProjectConfig(projectId);

        Map<String, Double> rankingLevels = config.getRankLevels();
        List<String> levelKeys = new ArrayList<>(rankingLevels.keySet());
        Collections.sort(levelKeys);

        double sum = 0, rankLevelValue = (double) rank / studentCount;
        for (String levelKey : levelKeys) {
            sum += rankingLevels.get(levelKey);
            if (rankLevelValue <= sum) {
                return levelKey;
            }
        }

        throw new IllegalStateException("无法找到排名等级: " +
                "project=" + projectId + ", range=" + range + ", target=" + target +
                ", student=" + studentId + ", rank=" + rank + ", levels=" + rankingLevels +
                ", studentCount=" + studentCount);
    }
}
