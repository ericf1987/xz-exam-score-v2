package com.xz.examscore.services;

import com.alibaba.fastjson.JSON;
import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
@Service
public class ProjectConfigService {

    public static final String DEFAULT = "[default]";

    public static final String[] ENTRY_LEVEL = new String[]{
            "ONE", "TWO", "THREE"
    };

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache instantCache;

    @Autowired
    RankService rankService;

    /**
     * 从缺省的配置模板产生一个新的项目配置
     *
     * @param projectId 项目ID
     */
    public void createProjectConfig(String projectId) {
        ProjectConfig template = getDefaultProjectConfig();
        template.setProjectId(projectId);
        replaceProjectConfig(template);
    }

    /**
     * 更改项目配置（替换原来的配置）
     *
     * @param projectConfig 要保存的项目配置
     */
    public void replaceProjectConfig(ProjectConfig projectConfig) {
        Document projectConfigDoc = Document.parse(JSON.toJSONString(projectConfig))
                .append("md5", MD5.digest(UUID.randomUUID().toString()));
        Document query = doc("projectId", projectConfig.getProjectId());
        MongoCollection<Document> collection = scoreDatabase.getCollection("project_config");

        collection.deleteMany(query);
        collection.insertOne(projectConfigDoc);
    }

    /**
     * 更新报表配置中的等第配置
     */
    public void updateRankLevelConfig(ProjectConfig projectConfig) {
        fixProjectConfig(projectConfig);
        MongoCollection<Document> collection = scoreDatabase.getCollection("project_config");
        UpdateResult result = collection.updateMany(doc("projectId", projectConfig.getProjectId()), $set(
                doc("combineCategorySubjects", projectConfig.isCombineCategorySubjects())
                        .append("rankLevels", projectConfig.getRankLevels())
                        .append("rankLevelCombines", projectConfig.getRankLevelCombines())
                        .append("scoreLevels", projectConfig.getScoreLevels())
                        .append("topStudentRate", projectConfig.getTopStudentRate())
                        .append("lastRankLevel", projectConfig.getLastRankLevel())
                        .append("rankSegmentCount", projectConfig.getRankSegmentCount())
                        .append("highScoreRate", projectConfig.getHighScoreRate())
                        .append("splitUnionSubject", projectConfig.isSeparateCombine())
                        .append("entryLevelStatType", projectConfig.getEntryLevelStatType())
                        .append("entryLevelEnable", projectConfig.isEntryLevelEnable())
                        .append("collegeEntryLevel", projectConfig.getCollegeEntryLevel())
        ));
        if (result.getMatchedCount() == 0) {
            collection.insertOne(doc("projectId", projectConfig.getProjectId())
                    .append("combineCategorySubjects", projectConfig.isCombineCategorySubjects())
                    .append("rankLevels", projectConfig.getRankLevels())
                    .append("rankLevelCombines", projectConfig.getRankLevelCombines())
                    .append("scoreLevels", projectConfig.getScoreLevels())
                    .append("topStudentRate", projectConfig.getTopStudentRate())
                    .append("lastRankLevel", projectConfig.getLastRankLevel())
                    .append("rankSegmentCount", projectConfig.getRankSegmentCount())
                    .append("highScoreRate", projectConfig.getHighScoreRate())
                    .append("splitUnionSubject", projectConfig.isSeparateCombine())
                    .append("entryLevelStatType", projectConfig.getEntryLevelStatType())
                    .append("entryLevelEnable", projectConfig.isEntryLevelEnable())
                    .append("collegeEntryLevel", projectConfig.getCollegeEntryLevel())
                    .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    /**
     * 更新报表配置中的等第配置
     *
     * @param projectId          项目ID
     * @param rankLevels         等第比例配置
     * @param isCombine          是否合并文理科
     * @param rankLevelCombines  展示的等第组合列表
     * @param scoreLevels        展示的分数等级
     * @param topStudentRate     展示的尖子生比例
     * @param highScoreRate      展示的高分段比例
     * @param splitUnionSubject  是否将综合科目拆分成单科统计
     * @param entryLevelStatType 本科上线率参数类型
     * @param entryLevelEnable   报表侧是否开启上线率报表
     * @param collegeEntryLevel  本科上线率参数
     */
    public void updateRankLevelConfig(
            String projectId, Map<String, Double> rankLevels, boolean isCombine,
            List<String> rankLevelCombines, Map<String, Double> scoreLevels, Double topStudentRate,
            String lastRankLevel, int rankSegmentCount, Double highScoreRate, Boolean splitUnionSubject,
            String entryLevelStatType, boolean entryLevelEnable, List<String> collegeEntryLevel) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("project_config");
        UpdateResult result = collection.updateMany(doc("projectId", projectId), $set(
                doc("combineCategorySubjects", isCombine)
                        .append("rankLevels", rankLevels)
                        .append("rankLevelCombines", rankLevelCombines)
                        .append("scoreLevels", scoreLevels)
                        .append("topStudentRate", topStudentRate)
                        .append("lastRankLevel", lastRankLevel)
                        .append("rankSegmentCount", rankSegmentCount)
                        .append("highScoreRate", highScoreRate)
                        .append("splitUnionSubject", splitUnionSubject)
                        .append("entryLevelStatType", entryLevelStatType)
                        .append("entryLevelEnable", entryLevelEnable)
                        .append("collegeEntryLevel", collegeEntryLevel)
        ));
        if (result.getMatchedCount() == 0) {
            collection.insertOne(doc("projectId", projectId)
                    .append("combineCategorySubjects", isCombine)
                    .append("rankLevels", rankLevels)
                    .append("rankLevelCombines", rankLevelCombines)
                    .append("scoreLevels", scoreLevels)
                    .append("topStudentRate", topStudentRate)
                    .append("lastRankLevel", lastRankLevel)
                    .append("rankSegmentCount", rankSegmentCount)
                    .append("highScoreRate", highScoreRate)
                    .append("splitUnionSubject", splitUnionSubject)
                    .append("entryLevelStatType", entryLevelStatType)
                    .append("entryLevelEnable", entryLevelEnable)
                    .append("collegeEntryLevel", collegeEntryLevel)
                    .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    /**
     * 获得缺省的项目配置模板
     *
     * @return 缺省的项目配置模板
     */
    public ProjectConfig getDefaultProjectConfig() {
        return getProjectConfig(DEFAULT);
    }

    /**
     * 获取指定项目的配置
     *
     * @param projectId 项目ID
     * @return 项目配置
     */
    public ProjectConfig getProjectConfig(String projectId) {
        String cacheKey = "project_config:" + projectId;

        return instantCache.get(cacheKey, () -> {
            Document document = scoreDatabase.getCollection("project_config")
                    .find(doc("projectId", projectId)).first();

            if (document == null) {
                return projectId.equals(DEFAULT) ? null : getProjectConfig(DEFAULT);
            } else {
                ProjectConfig projectConfig = JSON.toJavaObject(
                        JSON.parseObject(document.toJson()), ProjectConfig.class);

                return fixProjectConfig(projectConfig);
            }
        });
    }

    public ProjectConfig fixProjectConfig(ProjectConfig projectConfig) {

        if (projectConfig.getProjectId().equals(DEFAULT)) {
            return projectConfig;
        }

        ProjectConfig defaultConfig = getDefaultProjectConfig();

        //排名等级
        if (projectConfig.getRankLevels() == null || projectConfig.getRankLevels().isEmpty()) {
            projectConfig.setRankLevels(defaultConfig.getRankLevels());
        }

        //分数等级
        if (projectConfig.getScoreLevels() == null || projectConfig.getScoreLevels().isEmpty()) {
            projectConfig.setScoreLevels(defaultConfig.getScoreLevels());
        }

        //排名分段
        if (projectConfig.getRankSegmentCount() == 0) {
            projectConfig.setRankSegmentCount(defaultConfig.getRankSegmentCount());
        }

        //报表等第参数
        if (projectConfig.getRankLevelCombines() == null || projectConfig.getRankLevelCombines().isEmpty()) {
            projectConfig.setRankLevelCombines(defaultConfig.getRankLevelCombines());
        }

        //尖子生比率
        if (projectConfig.getTopStudentRate() == 0) {
            projectConfig.setTopStudentRate(defaultConfig.getTopStudentRate());
        }

        //高分段比例
        if (projectConfig.getHighScoreRate() == 0) {
            projectConfig.setHighScoreRate(defaultConfig.getHighScoreRate());
        }

        //本科上线率
        if (projectConfig.getCollegeEntryLevel().isEmpty()) {
            projectConfig.setCollegeEntryLevel(defaultConfig.getCollegeEntryLevel());
        }

        //统计方式为上线率或分数
        if (StringUtils.isEmpty(projectConfig.getEntryLevelStatType())) {
            projectConfig.setEntryLevelStatType(defaultConfig.getEntryLevelStatType());
        }

        return projectConfig;
    }

    public List<String> getRankLevelParams(String projectId, String subjectId) {
        ProjectConfig projectConfig = getProjectConfig(projectId);
        Map<String, Double> rankLevels = projectConfig.getRankLevels();

        Iterator<String> it = rankLevels.keySet().iterator();

        List<String> rankLevelParam = new ArrayList<>();
        while (it.hasNext()) {
            rankLevelParam.add(it.next());
        }

        return subjectId == null ? projectConfig.getRankLevelCombines() : rankLevelParam;
    }

    //将上线率转化为录取分数线的分数
    public List<Double> getEntryLevelScoreLine(String projectId, Range range, Target projectTarget, int studentCount) {
        ProjectConfig projectConfig = getProjectConfig(projectId);
        if (projectConfig.getEntryLevelStatType().equals("rate")) {
            //如果录取参数为排名率，则需要计算出对应排名位置的分数，根据此分数来录取
            return projectConfig.getCollegeEntryLevel().stream()
                    .map(rate -> getScoreByIndex(projectId, range, projectTarget, studentCount, rate)).collect(Collectors.toList());
        } else {
            //转化为得分
            return projectConfig.getCollegeEntryLevel().stream()
                    .map(rate -> DoubleUtils.round(Double.parseDouble(rate))).collect(Collectors.toList());
        }
    }

    //根据排名率计算排名位置的得分
    private double getScoreByIndex(String projectId, Range range, Target projectTarget, int studentCount, String rate) {
        double d = Double.parseDouble(rate) / 100;
        int index = (int) (studentCount * d);
        return rankService.getRankScore(projectId, range, projectTarget, index);
    }
}
