package com.xz.examscore.services;

import com.hyd.appserver.utils.StringUtils;
import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.SubjectObjective;
import com.xz.examscore.bean.Target;
import com.xz.examscore.cache.ProjectCacheManager;
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
import java.util.stream.Collectors;

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
    private MongoDatabase scoreDatabase;

    @Autowired
    private ProjectConfigService projectConfigService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectCombinationService subjectCombinationService;

    @Autowired
    private ImportProjectService importProjectService;

    @Autowired
    private ProjectCacheManager projectCacheManager;

    /**
     * 查询题目的答对人数
     *
     * @param projectId 项目ID
     * @param questId   题目ID
     * @param range     范围
     * @return 答对人数
     */
    public int getQuestCorrectCount(String projectId, String questId, Range range) {

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
    Document findOneJudgeQuestScore(String projectId, String questId) {
        Document query = doc("project", projectId).append("quest", questId).append("answer", $nin("*", null));
        return scoreDatabase.getCollection("score").find(query).first();
    }

    /**
     * 查询指定学生的所有分数记录
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @return 分数记录
     */
    List<Document> getStudentQuestScores(String projectId, String studentId) {
        List<String> allSubjects = new ArrayList<>();
        allSubjects.addAll(subjectService.querySubjects(projectId).stream().filter(subject -> subject.length() == 3).collect(Collectors.toList()));
        allSubjects.addAll(subjectCombinationService.getAllSubjectCombinations(projectId));
        List<Document> studentScores = new ArrayList<>();
        for (String subjectId : allSubjects) {
            studentScores.addAll(getStudentScoresBySubject(projectId, studentId, subjectId));
        }
        return studentScores;
    }

    /**
     * 查询指定学生的具体科目的所有分数记录
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param subjectId 学生ID
     * @return 分数记录
     */
    public FindIterable<Document> getStudentSubjectScores(String projectId, String studentId, String subjectId) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("score");
        Document query = doc("project", projectId).append("student", studentId).append("subject", subjectId);
        return collection.find(query);
    }

    /**
     * 查询指定学生的的得分明细总数
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param subjectId 学生ID
     * @return 分数记录
     */
    public long getStudentSubjectScoresCount(String projectId, String studentId, String subjectId, boolean isObjective) {
        String cacheKey = "quest_score_count:" + projectId + ":" + studentId + ":" + subjectId;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId).append("student", studentId).append("subject", subjectId).append("isObjective", isObjective);
            return collection.count(query);
        });
    }

    /**
     * 获取正确的题目数量
     *
     * @param projectId   项目ID
     * @param studentId   学生ID
     * @param subjectId   科目ID
     * @param isObjective 是否是客观题
     * @return 正确的题目数量
     */
    public long getQuestCorrectCount(String projectId, String studentId, String subjectId, boolean isObjective) {

        String cacheKey = "quest_score_count:" + projectId + ":" + studentId + ":" + subjectId + ":" + isObjective;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId).append("student", studentId).append("subject", subjectId).append("isObjective", isObjective).append("right", true);
            return collection.count(query);
        });

    }

    /**
     * 获取错题题号
     *
     * @param projectId   项目ID
     * @param studentId   学生ID
     * @param subjectId   科目ID
     * @param isObjective 是否是客观题
     * @param isRight     是否正确
     * @return 错题题号
     */
    public List<String> getErrorQuestNo(String projectId, String studentId, String subjectId, boolean isObjective, boolean isRight) {
        String cacheKey = "quest_score_questNo:" + projectId + ":" + studentId + ":" + subjectId + ":" + isObjective + ":" + isRight;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId).append("student", studentId).append("subject", subjectId).append("isObjective", isObjective).append("right", isRight);
            Document projection = doc("questNo", 1);
            FindIterable<Document> findIterable = collection.find(query).projection(projection);
            List<String> questNo = toList(findIterable).stream().map(l -> l.getString("questNo")).collect(Collectors.toList());
            return CollectionUtils.asArrayList(questNo);
        });
    }

    /**
     * 查询指定学生的具体科目的所有分数记录列表
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param subjectId 学生ID
     * @return 分数记录
     */
    public ArrayList<Document> getStudentScoresBySubject(String projectId, String studentId, String subjectId) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("score");
        if (subjectId.length() > ImportProjectService.SUBJECT_LENGTH) {
            //组合科目
            Document query = doc("project", projectId).append("student", studentId).append("subject", subjectId);
            Document first = collection.find(query).first();
            //如果存在组合科目的分数
            if (null != first && !first.isEmpty()) {
                return CollectionUtils.asArrayList(toList(collection.find(query)));
            } else {
                //查询单科
                List<String> subjects = importProjectService.separateSubject(subjectId);
                List<Document> list = new ArrayList<>();
                for (String subject : subjects) {
                    Document q = doc("project", projectId).append("student", studentId).append("subject", subject);
                    FindIterable<Document> documents = collection.find(q);
                    list.addAll(toList(documents));
                }
                list.forEach(doc -> doc.put("subject", subjectId));
                return CollectionUtils.asArrayList(list);
            }
        }
        Document query = doc("project", projectId).append("student", studentId).append("subject", subjectId);
        return CollectionUtils.asArrayList(toList(collection.find(query)));
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

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId).append("student", studentId).append("quest", questId);
            Document document = collection.find(query).first();
            return document == null ? 0d : document.getDouble("score");
        });
    }

    public Document getScoreDoc(String projectId, String studentId, String questId, boolean isObjective) {
        String cacheKey = "quest_score:" + projectId + ":" + studentId + ":" + questId + ":" + isObjective;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
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

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            return getTotalScore0(collection, projectId, range, target);
        });
    }

    private Double getTotalScore0(String collection, String projectId, Range range, Target target) {

        MongoCollection<Document> totalScores = scoreDatabase.getCollection(collection);

        Document query;

        if(target.match(Target.SUBJECT_OBJECTIVE)){
            SubjectObjective subjectObjective = target.getId(SubjectObjective.class);
            query = doc("project", projectId)
                    .append("range", range2Doc(range))
                    .append("target.name", Target.SUBJECT_OBJECTIVE)
                    .append("target.id.subject", subjectObjective.getSubject())
                    .append("target.id.objective", subjectObjective.isObjective());
        }else {
            query = Mongo.query(projectId, range, target);
        }
/*
        Document query = new Document("project", projectId)
                .append("range", range2Doc(range))
                .append("target", target2Doc(target));*/

        List<Document> docs = MongoUtils.toList(totalScores.find(query).projection(doc("totalScore", 1)));

        double result = 0;
        for (Document doc : docs) {
            if (doc.get("totalScore") != null) {
                result += doc.getDouble("totalScore");
            }
        }

        return result;
    }

    public Map<String, Double> getAllSubjectScore(String projectId, Range range) {
        List<String> subjectIds = subjectService.querySubjects(projectId);
        Map<String, Double> result = new HashMap<>();

        for (String subjectId : subjectIds) {
            result.put(subjectId, getScore(projectId, range, Target.subject(subjectId)));
        }

        return result;
    }

    /**
     * 保存一条总分记录
     *
     * @param projectId  项目ID
     * @param range      范围
     * @param target     目标
     * @param totalScore 总分值
     */
    public void saveTotalScore(
            String projectId, Range range, Target target, double totalScore, Document extra) {

        String collectionName = getTotalScoreCollection(projectId, target);
        Document query = Mongo.query(projectId, range, target);
        Document update = doc("totalScore", totalScore);

        if (extra != null) {
            update.putAll(extra);
        }

        UpdateResult result = scoreDatabase.getCollection(collectionName).updateMany(query, $set(update));
        if (result.getMatchedCount() == 0) {
            query.putAll(update);
            scoreDatabase.getCollection(collectionName).insertOne(query.append("md5", Mongo.md5()));
        }
        String cacheKey = "score:" + collectionName + ":" + projectId + ":" + range + ":" + target;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        simpleCache.delete(cacheKey);
    }

    /**
     * 查询高于指定分数的记录总数
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param score     分数
     * @return 高于指定分数的记录总数
     */
    public int getCountByScore(String projectId, Range range, Target target, double score) {
        return getListByScore(projectId, range, target, score).size();
    }

    public List<Document> getListByScore(String projectId, Range range, Target target, double score) {
        String collectionName = getTotalScoreCollection(projectId, target);
        String cacheKey = "listByScore:" + collectionName + ":" + projectId + ":" + range + ":" + target + ":" + score;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            Document query = doc("project", projectId).append("range.name", Range.STUDENT)
                    .append(range.getName(), range.getId())
                    .append("target", target2Doc(target))
                    .append("totalScore", $gte(score));
            FindIterable<Document> documents = scoreDatabase.getCollection(collectionName).find(query);
            return new ArrayList<>(toList(documents));
        });
    }

    /**
     * 获取分数段内的学生人数
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @param max       最大值
     * @param min       最小值
     * @return 分数段内的学生人数
     */
    public int getCountByScoreSpan(String projectId, Range range, Target target, double max, double min) {
        String collectionName = getTotalScoreCollection(projectId, target);
        String cacheKey = "countByScoreSpan:" + collectionName + ":" + projectId + ":" + range + ":" + target + ":" + max + ":" + min;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        Document doc = new Document();
        if (min != 0) {
            doc.append("$gte", min);
        }
        if (max != 0) {
            doc.append("$lt", max);
        }

        return simpleCache.get(cacheKey, () -> {
            Document query = doc("project", projectId).append("range.name", Range.STUDENT)
                    .append(range.getName(), range.getId())
                    .append("target", target2Doc(target))
                    .append("totalScore", doc);
            return (int) scoreDatabase.getCollection(collectionName).count(query);
        });
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

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        simpleCache.delete(cacheKey);
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

    public ArrayList<Document> getScoreDocs(String projectId, Range range, String subjectId, String questId, String item) {
        String cacheKey = "quest_score:" + projectId + ":" + range + ":" + subjectId + ":" + questId + ":" + item;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId)
                    .append(range.getName(), range.getId())
                    .append("subject", subjectId)
                    .append("quest", questId)
                    .append("answer", item);
            return CollectionUtils.asArrayList(toList(collection.find(query)));
        });
    }

    /**
     * 查询分数段内的记录
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param subjectId 科目ID
     * @param questId   题目ID
     * @param min       最小分值
     * @param max       最大分值
     * @return 返回记录
     */
    public ArrayList<Document> getScoreDocsByScoreSegment(String projectId, Range range, String subjectId, String questId, Double min, Double max) {
        String cacheKey = "quest_score:" + projectId + ":" + range + ":" + subjectId + ":" + questId + ":" + min + ":" + max;
        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);
        Document query = doc("project", projectId)
                .append(range.getName(), range.getId())
                .append("subject", subjectId)
                .append("quest", questId);
        if (min == 0) {
            query.append("score", doc("$gte", min).append("$lte", max));
        } else {
            query.append("score", doc("$gt", min).append("$lte", max));
        }
        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            return CollectionUtils.asArrayList(toList(collection.find(query)));
        });
    }

    /**
     * 查询试题作答的学生数
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param subjectId 科目ID
     * @param questId   题目ID
     * @return 学生数
     */
    public int getScoreRecordCount(String projectId, Range range, String subjectId, String questId) {
        String cacheKey = "score_quest_count:" + projectId + ":" + range + ":" + subjectId + ":" + questId;
        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);
        return simpleCache.get(cacheKey, () -> {
            Document query = doc("project", projectId).append(range.getName(), range.getId())
                    .append("subject", subjectId).append("quest", questId);
            return (int) scoreDatabase.getCollection("score").count(query);
        });
    }

    /**
     * 判断学生是否缺考
     *
     * @param projectId 项目ID
     * @param studentId 学生ID
     * @param target    目标
     * @return 是否缺考
     */
    public boolean isStudentAbsent(String projectId, String studentId, Target target) {
        String targetName = target.getName();
        MongoCollection<Document> collection = scoreDatabase.getCollection("score");
        Document query = doc("project", projectId).append("student", studentId);
        switch (targetName) {
            case Target.PROJECT:
                //该学生全部科目全部标记为缺考，则判定为项目缺考
                query.append("isAbsent", $exists(false));
                long count = collection.count(query);
                return count == 0;
            case Target.SUBJECT:
                query.append("subject", target.getId().toString());
                Document doc = collection.find(query).first();
                return null == doc || BooleanUtils.toBoolean(doc.getBoolean("isAbsent"));
            default:
                return false;
        }
    }

    /**
     * 根据目标列表获取得分信息
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param targetIds 目标ID集合
     * @return 分数集合
     */
    public ArrayList<Document> getTotalScoreByTargetIds(String projectId, Range range, List<String> targetIds) {
        String cacheKey = "getTotalScoreByTargetIds:" + projectId + ":" + range + ":" + targetIds.toString();

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("total_score");
            Document query = doc("project", projectId);
            if (null != range) {
                query.append("range.id", range.getId());
            }
            if (!targetIds.isEmpty()) {
                query.append("target.id", $in(targetIds));
            }
            return CollectionUtils.asArrayList(toList(collection.find(query).projection(doc("range", 1).append("target", 1).append("totalScore", 1))));
        });
    }

    /**
     * 根据科目和题目ID查询得分
     *
     * @param projectId 考试ID
     * @param subjectId 科目ID
     * @return 分数集合
     */
    public List<Document> getScoreAndQuestId(String projectId, String subjectId) {
        String cacheKey = "getScoreAndQuestId:" + projectId + ":" + subjectId;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("score");
            Document query = doc("project", projectId);
            if (!StringUtils.isBlank(subjectId)) {
                query.append("subject", subjectId);
            }
            return CollectionUtils.asArrayList(
                    toList(collection.find(query).projection(
                            doc("quest", 1).append("student", 1).append("subject", 1).append("score", 1).append("isObjective", 1).append("questNo", 1)
                    ))
            );
        });
    }
}
