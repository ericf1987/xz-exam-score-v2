package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

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

    @Autowired
    SimpleCache cache;

    @Autowired
    ImportProjectService importProjectService;

    /**
     * 查询排名
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param studentId 学生ID
     * @return 分数在指定目标和范围内的排名
     */
    public int getRank(String projectId, Range range, Target target, String studentId) {
        String cacheKey = "student_rank:" + projectId + ":" + range + ":" + target + ":" + studentId;

        return cache.get(cacheKey, () -> {
            double score = scoreService.getScore(projectId, Range.student(studentId), target);
            return getRank(projectId, range, target, score);
        });
    }

    /**
     * 查询排名
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param score     分数
     * @return 分数在指定目标和范围内的排名
     */
    public int getRank(String projectId, Range range, Target target, double score) {

        String cacheKey = "score_rank:" + projectId + ":" + range + ":" + target + ":" + score;
        return cache.get(cacheKey, () -> {
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
        });
    }

    /**
     * 查询排名位置的得分
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param rankIndex 分数
     * @return 查询排名位置的得分
     */
    public double getRankScore(String projectId, Range range, Target target, int rankIndex) {
        String cacheKey = "rank_score:" + projectId + ":" + range + ":" + target + ":" + rankIndex;

        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score_map");
            Document id = Mongo.query(projectId, range, target);
            Document doc = collection.find(id).first();
            if (null != doc && !doc.isEmpty()) {
                List<Document> scoreMap = (List<Document>) doc.get("scoreMap");
                Collections.sort(scoreMap, (Map<String, Object> m1, Map<String, Object> m2) -> {
                    Double s1 = (Double) m1.get("score");
                    Double s2 = (Double) m2.get("score");
                    return s2.compareTo(s1);
                });
                int rankCount = 0;
                for (Document one : scoreMap) {
                    rankCount += one.getInteger("count");
                    if (rankCount >= rankIndex) {
                        return one.getDouble("score");
                    }
                }
            }
            return 0d;
        });

    }

    /**
     * 删除指定项目的排名统计结果（为了重新统计排名）
     *
     * @param projectId 项目ID
     */
    public void deleteRanks(String projectId) {
        scoreDatabase.getCollection("score_map").deleteMany(doc("project", projectId));
    }

    /**
     * 查询考生分数的排名等级
     *
     * @param projectId 项目ID
     * @param range     排名范围
     * @param target    排名目标
     * @param studentId 学生ID
     * @return 排名等级，如果考生没有参加考试则返回 null
     */
    public String getRankLevel(String projectId, Range range, Target target, String studentId) {
        if (target.getName().equals(Target.SUBJECT_COMBINATION)) {
            return getRankLevelWithSubjectCombination(projectId, range, target, studentId);
        } else {
            return getRankLevelNonSubjectCombination(projectId, range, target, studentId);
        }
    }

    //获取费
    private String getRankLevelNonSubjectCombination(String projectId, Range range, Target target, String studentId) {
        int rank = getRank(projectId, range, target, studentId);
        int studentCount = studentService.getStudentCount(projectId, range, target);

        ProjectConfig config = projectConfigService.getProjectConfig(projectId);

        if (rank > studentCount) {  // 说明没有参加考试
            return null;
        }

        Map<String, Double> rankingLevels = config.getRankLevels();
        List<String> levelKeys = new ArrayList<>(rankingLevels.keySet());
        Collections.sort(levelKeys);

        double sum = 0, rankLevelValue = studentCount == 0 ? 0 : ((double) rank / studentCount);
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

    //获取组合科目的排名等级
    private String getRankLevelWithSubjectCombination(String projectId, Range range, Target target, String studentId) {
        String subjectCombinationId = target.getId().toString();
        List<Target> subjectCombinationsTarget = importProjectService.separateSubject(subjectCombinationId).stream().map(
                Target::subject
        ).collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        for (Target subjectTarget : subjectCombinationsTarget) {
            String levelKey = getRankLevelNonSubjectCombination(projectId, range, subjectTarget, studentId);
            if (null == levelKey) {
                return null;
            }
            builder.append(levelKey);
        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public List<Document> getScoreMap(String projectId, Range range, Target target) {

        Document doc = scoreDatabase.getCollection("score_map").find(
                doc("project", projectId).append("range", range2Doc(range)).append("target", target2Doc(target))
        ).first();

        if (doc == null) {
            return Collections.emptyList();
        } else {
            return (List<Document>) doc.get("scoreMap");
        }
    }
}
