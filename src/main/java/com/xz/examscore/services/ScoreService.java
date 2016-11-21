package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.Mongo;
import com.xz.examscore.util.SubjectUtil;
import org.apache.commons.lang.BooleanUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
@Service
public class ScoreService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SimpleCache cache;

    /**
     * 查询题目的答对人数
     *
     * @param projectId 项目ID
     * @param questId   题目ID
     * @param range     范围
     * @return 答对人数
     */
    public int getQuestCorrentCount(String projectId, String questId, Range range) {

        Document query = doc("project", projectId)
                .append("quest", questId)
                .append(range.getName(), range.getId())
                .append("right", true);

        return (int) scoreDatabase.getCollection("score").count(query);
    }

    /**
     * 查询分数
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @return 分数
     */
    public double getScore(String projectId, Range range, Target target) {

        // 1. 学生的题目得分从 score 查询;
        // 2. 文理合计的得分从 total_score_combined 查询;
        // 3. 其他得分从 total_score 查询

        if (isQueryQuestScore(range, target)) {
            return getQuestScore(projectId, range.getId(), target.getId().toString());
        } else {
            return getTotalScore(projectId, range, target);
        }
    }

    /**
     * 查询指定判断题的任意一个分数
     *
     * @param projectId 项目ID
     * @param questId   题目ID
     * @return 分数记录
     */
    public Document findOneJudgeQuestScore(String projectId, String questId) {
        Document query = doc("project", projectId).append("quest", questId).append("answer", $nin("*", null));
        return scoreDatabase.getCollection("score").find(query).first();
    }

    /**
     * 查询一个学生在指定项目中的指定目标名称的分数
     *
     * @param projectId  项目ID
     * @param studentId  学生ID
     * @param targetName 目标名称
     * @return 分数列表
     */
    public List<Document> getStudentScores(String projectId, String studentId, String targetName) {

        if (targetName.equals(Target.QUEST)) {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId).append("student", studentId);
            return MongoUtils.toList(collection.find(query));

        } else {

            MongoCollection<Document> collection;
            List<Document> result = new ArrayList<>();
            Document query = doc("project", projectId).append("range", range2Doc(Range.student(studentId)));

            collection = scoreDatabase.getCollection("total_score");
            result.addAll(MongoUtils.toList(collection.find(query)));

            collection = scoreDatabase.getCollection("total_score_combined");
            result.addAll(MongoUtils.toList(collection.find(query)));

            return result;
        }
    }

    /**
     * 查询指定学生的所有分数记录
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @return 分数记录
     */
    public FindIterable<Document> getStudentQuestScores(String projectId, String studentId) {
        Document query = doc("project", projectId).append("student", studentId);
        return scoreDatabase.getCollection("score").find(query);
    }

    /**
     * 查询科目成绩
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param subjectId 科目ID
     * @return 成绩
     */
    public double getSubjectScore(String projectId, String studentId, String subjectId) {
        return getScore(projectId, Range.student(studentId), Target.subject(subjectId));
    }

    private boolean isQueryQuestScore(Range range, Target target) {
        return range.match(Range.STUDENT) && target.match(Target.QUEST);
    }

    //////////////////////////////////////////////////////////////

    private double getQuestScore(String projectId, String studentId, String questId) {
        String cacheKey = "quest_score:" + projectId + ":" + studentId + ":" + questId;

        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId).append("student", studentId).append("quest", questId);
            Document document = collection.find(query).first();
            return document == null ? 0d : document.getDouble("score");
        });
    }

    public Document getScoreDoc(String projectId, String studentId, String questId, boolean isObjective) {
        String cacheKey = "quest_score:" + projectId + ":" + studentId + ":" + questId + ":" + isObjective;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId)
                    .append("student", studentId)
                    .append("quest", questId)
                    .append("isObjective", isObjective);
            return collection.find(query).first();
        });
    }

    private double getTotalScore(String projectId, Range range, Target target) {
        String collectionName = getTotalScoreCollection(projectId, target);
        return getTotalScore(collectionName, projectId, range, target);
    }

    private double getTotalScore(String collection, String projectId, Range range, Target target) {
        String cacheKey = "score:" + collection + ":" + projectId + ":" + range + ":" + target;

        return cache.get(cacheKey, () -> {
            return getTotalScore0(collection, projectId, range, target);
        });
    }

    private Double getTotalScore0(String collection, String projectId, Range range, Target target) {

        MongoCollection<Document> totalScores = scoreDatabase.getCollection(collection);

        Document query = new Document("project", projectId)
                .append("range", range2Doc(range))
                .append("target", target2Doc(target));

        List<Document> docs = MongoUtils.toList(totalScores.find(query).projection(doc("totalScore", 1)));

        double result = 0;
        for (Document doc : docs) {
            if (doc.get("totalScore") != null) {
                result += doc.getDouble("totalScore");
            }
        }

        return result;
    }

    public Double getTotalScore0(String projectId, Range range, Target target) {
        String collection = getTotalScoreCollection(projectId, target);
        return getTotalScore0(collection, projectId, range, target);
    }

    public Map<String, Double> getAllSubjectScore(String projectId, Range range) {
        List<String> subjectIds = subjectService.querySubjects(projectId);
        Map<String, Double> result = new HashMap<>();

        for (String subjectId : subjectIds) {
            result.put(subjectId, getScore(projectId, range, Target.subject(subjectId)));
        }

        return result;
    }

    public void saveTotalScore(
            String projectId, Range range, Range parent, Target target, double score, Document extra) {

        String collectionName = getTotalScoreCollection(projectId, target);
        Document query = Mongo.query(projectId, range, target);
        Document update = doc("totalScore", score);

        if (parent != null) {
            update.append("parent", range2Doc(parent));
        }

        if (extra != null) {
            update.putAll(extra);
        }

        UpdateResult result = scoreDatabase.getCollection(collectionName).updateMany(query, $set(update));
        if (result.getMatchedCount() == 0) {
            query.putAll(update);
            scoreDatabase.getCollection(collectionName).insertOne(query.append("md5", Mongo.md5()));
        }
        String cacheKey = "score:" + collectionName + ":" + projectId + ":" + range + ":" + target;
        cache.delete(cacheKey);
    }

    /**
     * 创建一条空的总分记录
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     */
    public void createTotalScore(String projectId, Range range, Target target) {
        String collectionName = getTotalScoreCollection(projectId, target);
        scoreDatabase.getCollection(collectionName).insertOne(
                Mongo.query(projectId, range, target).append("totalScore", 0.0).append("md5", Mongo.md5()));
    }

    /**
     * 累加总分
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param score     分数
     */
    public int addTotalScore(String projectId, Range range, Target target, double score) {
        String collectionName = getTotalScoreCollection(projectId, target);
        String cacheKey = "score:" + collectionName + ":" + projectId + ":" + range + ":" + target;

        Document query = Mongo.query(projectId, range, target);
        MongoCollection<Document> col = scoreDatabase.getCollection(collectionName);
        UpdateResult result = col.updateMany(query, $inc("totalScore", score));

        cache.delete(cacheKey);
        return (int) result.getModifiedCount();
    }

    public String getTotalScoreCollection(String projectId, Target target) {
        ProjectConfig config = projectConfigService.getProjectConfig(projectId);
        if (config.isCombineCategorySubjects() && SubjectUtil.isCombinedSubject(target)) {
            return "total_score_combined";
        } else {
            return "total_score";
        }
    }

    public void clearByTargetName(String projectId, String targetName) {
        Document query = doc("project", projectId).append("target.name", targetName);
        scoreDatabase.getCollection("total_score_combined").deleteMany(query);
        scoreDatabase.getCollection("total_score").deleteMany(query);
    }

    public ArrayList<Document> getScoreDocs(String projectId, Range range, String subjectId, String questId, String item) {
        String cacheKey = "quest_score:" + projectId + ":" + range + ":" + subjectId + ":" + questId + ":" + item;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId)
                    .append(range.getName(), range.getId())
                    .append("subject", subjectId)
                    .append("quest", questId)
                    .append("answer", item);
            return CollectionUtils.asArrayList(toList(collection.find(query)));
        });
    }

    public ArrayList<Document> getScoreDocsByScoreSegment(String projectId, Range range, String subjectId, String questId, Double min, Double max) {
        String cacheKey = "quest_score:" + projectId + ":" + range + ":" + subjectId + ":" + questId + ":" + min + ":" + max;
        Document query = doc("project", projectId)
                .append(range.getName(), range.getId())
                .append("subject", subjectId)
                .append("quest", questId);
        if (min == 0) {
            query.append("score", doc("$gte", min).append("$lte", max));
        } else {
            query.append("score", doc("$gt", min).append("$lte", max));
        }
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            return CollectionUtils.asArrayList(toList(collection.find(query)));
        });
    }

    //查询试题作答的学生数
    public int getScoreRecordCount(String projectId, Range range, String subjectId, String questId) {
        String cacheKey = "score_quest_count:" + projectId + ":" + range + ":" + subjectId + ":" + questId;
        return cache.get(cacheKey, () -> {
            Document query = doc("project", projectId).append(range.getName(), range.getId())
                    .append("subject", subjectId).append("quest", questId);
            return (int)scoreDatabase.getCollection("score").count(query);
        });
    }

    //判断学生是否缺考
    public boolean isStudentAbsent(String projectId, String studentId, Target target) {
        String targetName = target.getName();
        MongoCollection<Document> collection = scoreDatabase.getCollection("score");
        Document query = doc("project", projectId).append("student", studentId);
        if(targetName.equals(Target.PROJECT)){
            //只有当没有任何分数明细的时候，才判断考生整个考试项目为缺考状态
            long count = collection.count(query);
            if(count == 0){
                return true;
            }
            return false;
        }else if(targetName.equals(Target.SUBJECT)){
            query.append("subject", target.getId().toString());
            Document doc = collection.find(query).first();
            if(null == doc){
                return true;
            }
            return BooleanUtils.toBoolean(doc.getBoolean("isAbsent"));
        }else{
            return false;
        }
    }

}
