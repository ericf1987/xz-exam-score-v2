package com.xz.services;

import com.alibaba.fastjson.JSON;
import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.ProjectConfig;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
@Service
public class ProjectConfigService {

    public static final String DEFAULT = "[default]";

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

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
        Document projectConfigDoc = Document.parse(JSON.toJSONString(projectConfig));
        Document query = doc("projectId", projectConfig.getProjectId());
        MongoCollection<Document> collection = scoreDatabase.getCollection("project_config");

        collection.deleteMany(query);
        collection.insertOne(projectConfigDoc);
    }


    /**
     * 更新报表配置中的等第配置
     *
     * @param projectId         项目ID
     * @param rankLevels        等第比例配置
     * @param isCombine         是否合并文理科
     * @param rankLevelCombines 展示的等第组合列表
     */
    public void updateRankLevelConfig(
            String projectId, Map<String, Double> rankLevels, boolean isCombine, List<String> rankLevelCombines) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("project_config");
        collection.updateMany(doc("projectId", projectId), $set(
                doc("combineCategorySubjects", isCombine)
                        .append("rankLevels", rankLevels)
                        .append("rankLevelCombines", rankLevelCombines)
        ), UPSERT);
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

        return cache.get(cacheKey, () -> {
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

        if (projectConfig.getRankLevels() == null || projectConfig.getRankLevels().isEmpty()) {
            projectConfig.setRankLevels(defaultConfig.getRankLevels());
        }

        if (projectConfig.getScoreLevels() == null || projectConfig.getScoreLevels().isEmpty()) {
            projectConfig.setScoreLevels(defaultConfig.getScoreLevels());
        }

        if (projectConfig.getRankSegmentCount() == 0) {
            projectConfig.setRankSegmentCount(defaultConfig.getRankSegmentCount());
        }

        if (projectConfig.getRankLevelCombines() == null || projectConfig.getRankLevelCombines().isEmpty()) {
            projectConfig.setRankLevelCombines(defaultConfig.getRankLevelCombines());
        }

        if (projectConfig.getTopStudentRate() == 0) {
            projectConfig.setTopStudentRate(defaultConfig.getTopStudentRate());
        }

        return projectConfig;
    }
}
