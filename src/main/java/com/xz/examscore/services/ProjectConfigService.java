package com.xz.examscore.services;

import com.alibaba.fastjson.JSON;
import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.lang.StringUtil;
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
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;

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
                        .append("separateCombine", projectConfig.isSeparateCombine())
                        .append("entryLevelStatType", projectConfig.getEntryLevelStatType())
                        .append("entryLevelEnable", projectConfig.isEntryLevelEnable())
                        .append("collegeEntryLevel", projectConfig.getCollegeEntryLevel())
                        .append("shareSchoolReport", projectConfig.isShareSchoolReport())
                        .append("almostPassOffset", projectConfig.getAlmostPassOffset())
                        .append("fillAlmostPass", projectConfig.isFillAlmostPass())
                        .append("removeAbsentStudent", projectConfig.isRemoveAbsentStudent())
                        .append("removeZeroScores", projectConfig.isRemoveZeroScores())
                        .append("removeCheatStudent", projectConfig.isRemoveCheatStudent())
                        .append("scoreLevelConfig", projectConfig.getScoreLevelConfig())
                        .append("allowPaperMark", projectConfig.isAllowPaperMark())
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
                    .append("separateCombine", projectConfig.isSeparateCombine())
                    .append("entryLevelStatType", projectConfig.getEntryLevelStatType())
                    .append("entryLevelEnable", projectConfig.isEntryLevelEnable())
                    .append("collegeEntryLevel", projectConfig.getCollegeEntryLevel())
                    .append("shareSchoolReport", projectConfig.isShareSchoolReport())
                    .append("almostPassOffset", projectConfig.getAlmostPassOffset())
                    .append("fillAlmostPass", projectConfig.isFillAlmostPass())
                    .append("removeAbsentStudent", projectConfig.isRemoveAbsentStudent())
                    .append("removeZeroScores", projectConfig.isRemoveZeroScores())
                    .append("removeCheatStudent", projectConfig.isRemoveCheatStudent())
                    .append("scoreLevelConfig", projectConfig.getScoreLevelConfig())
                    .append("allowPaperMark", projectConfig.isAllowPaperMark())
                    .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    /**
     * 更新报表配置中的等第配置
     *
     * @param projectId           项目ID
     * @param rankLevels          等第比例配置
     * @param isCombine           是否合并文理科
     * @param rankLevelCombines   展示的等第组合列表
     * @param scoreLevels         展示的分数等级
     * @param topStudentRate      展示的尖子生比例
     * @param highScoreRate       展示的高分段比例
     * @param splitUnionSubject   是否将综合科目拆分成单科统计
     * @param entryLevelStatType  本科上线率参数类型
     * @param entryLevelEnable    报表侧是否开启上线率报表
     * @param collegeEntryLevel   本科上线率参数
     * @param shareSchoolReport   是否开启学校信息共享
     * @param almostPassOffset    比及格分低多少分以内算作及格
     * @param fillAlmostPass      是否将接近及格的分数设为及格
     * @param removeAbsentStudent 是否排除缺考记录
     * @param removeZeroScores    是否排除0分记录
     * @param removeCheatStudent  是否排除违纪学生
     * @param scoreLevelConfig    分数等级配置
     * @param allowPaperMark      是否允许查看试卷留痕
     */
    public void updateRankLevelConfig(
            String projectId, Map<String, Double> rankLevels, boolean isCombine,
            List<String> rankLevelCombines, Map<String, Object> scoreLevels, Double topStudentRate,
            String lastRankLevel, int rankSegmentCount, Double highScoreRate, Boolean splitUnionSubject,
            String entryLevelStatType, boolean entryLevelEnable, List<String> collegeEntryLevel, boolean shareSchoolReport,
            String almostPassOffset, boolean fillAlmostPass, boolean removeAbsentStudent, boolean removeZeroScores,
            boolean removeCheatStudent, String scoreLevelConfig, boolean allowPaperMark) {
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
                        .append("separateCombine", splitUnionSubject)
                        .append("entryLevelStatType", entryLevelStatType)
                        .append("entryLevelEnable", entryLevelEnable)
                        .append("collegeEntryLevel", collegeEntryLevel)
                        .append("shareSchoolReport", shareSchoolReport)
                        .append("almostPassOffset", almostPassOffset)
                        .append("fillAlmostPass", fillAlmostPass)
                        .append("removeAbsentStudent", removeAbsentStudent)
                        .append("removeZeroScores", removeZeroScores)
                        .append("removeCheatStudent", removeCheatStudent)
                        .append("scoreLevelConfig", scoreLevelConfig)
                        .append("allowPaperMark", allowPaperMark)
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
                    .append("separateCombine", splitUnionSubject)
                    .append("entryLevelStatType", entryLevelStatType)
                    .append("entryLevelEnable", entryLevelEnable)
                    .append("collegeEntryLevel", collegeEntryLevel)
                    .append("shareSchoolReport", shareSchoolReport)
                    .append("md5", MD5.digest(UUID.randomUUID().toString()))
                    .append("almostPassOffset", almostPassOffset)
                    .append("fillAlmostPass", fillAlmostPass)
                    .append("removeAbsentStudent", removeAbsentStudent)
                    .append("removeZeroScores", removeZeroScores)
                    .append("removeCheatStudent", removeCheatStudent)
                    .append("scoreLevelConfig", scoreLevelConfig)
                    .append("allowPaperMark", allowPaperMark)
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
    public double getScoreByIndex(String projectId, Range range, Target projectTarget, int studentCount, String rate) {
        double d = Double.parseDouble(rate) / 100;
        int index = (int) (studentCount * d);
        return rankService.getRankScore(projectId, range, projectTarget, index);
    }

    public Map<String, Object> getScoreLevelByConfig(Target target, ProjectConfig projectConfig) {
        String scoreLevelConfig = projectConfig.getScoreLevelConfig();
        Map<String, Object> scoreLevels = projectConfig.getScoreLevels();

        Map<String, Object> scoreLevelsMap = new HashMap<>();
        if (target.match(Target.SUBJECT) || target.match(Target.PROJECT)) {
            packScoreLevelByConfig2(scoreLevelConfig, scoreLevels, scoreLevelsMap);
        }

        return scoreLevelsMap;
    }

    public void packScoreLevelByConfig(String scoreLevelConfig, Map<String, Object> scoreLevels, Map<String, Object> scoreLevelsMap) {
        if (!StringUtil.isBlank(scoreLevelConfig) && scoreLevelConfig.equals("score")) {
            for (String subjectKey : scoreLevels.keySet()) {
                scoreLevelsMap.put(subjectKey, fixScoreLevelKey((Map<String, Object>) scoreLevels.get(subjectKey)));
            }
        } else {
            scoreLevelsMap.putAll(fixScoreLevelKey(scoreLevels));
        }
    }

    public void packScoreLevelByConfig2(String scoreLevelConfig, Map<String, Object> scoreLevels, Map<String, Object> scoreLevelsMap) {
        if (!StringUtil.isBlank(scoreLevelConfig) && scoreLevelConfig.equals("score")) {
            for (String subjectKey : scoreLevels.keySet()) {
                scoreLevelsMap.put(subjectKey, scoreLevels.get(subjectKey));
            }
        } else {
            scoreLevelsMap.put(Excellent.name(), scoreLevels.get(Excellent.name()));
            scoreLevelsMap.put(Good.name(), scoreLevels.get(Good.name()));
            scoreLevelsMap.put(Pass.name(), scoreLevels.get(Pass.name()));
            scoreLevelsMap.put(Fail.name(), scoreLevels.get(Fail.name()));
        }
    }

    //修复CMS导出三率时KEY的大小写问题
    private Map<String, Object> fixScoreLevelKey(Map<String, Object> scoreLevels) {
        Map<String, Object> scoreLevelMap = new HashMap<>();
        scoreLevelMap.put(Excellent.name(), scoreLevels.get("excellent"));
        scoreLevelMap.put(Good.name(), scoreLevels.get("good"));
        scoreLevelMap.put(Pass.name(), scoreLevels.get("pass"));
        scoreLevelMap.put(Fail.name(), scoreLevels.get("fail"));
        return scoreLevelMap;
    }
}
