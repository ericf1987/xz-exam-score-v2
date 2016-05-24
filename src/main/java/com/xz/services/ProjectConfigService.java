package com.xz.services;

import com.alibaba.fastjson.JSON;
import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.ProjectConfig;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        saveProjectConfig(template);
    }

    /**
     * 保存项目配置
     *
     * @param projectConfig 要保存的项目配置
     */
    public void saveProjectConfig(ProjectConfig projectConfig) {
        Document projectConfigDoc = Document.parse(JSON.toJSONString(projectConfig));
        Document query = doc("projectId", projectConfig.getProjectId());
        MongoCollection<Document> collection = scoreDatabase.getCollection("project_config");

        collection.deleteMany(query);
        collection.insertOne(projectConfigDoc);
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
     *
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

    private ProjectConfig fixProjectConfig(ProjectConfig projectConfig) {

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

        return projectConfig;
    }
}
