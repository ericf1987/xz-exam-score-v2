package com.xz.services;

import com.alibaba.fastjson.JSON;
import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
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

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    public void saveProjectConfig(ProjectConfig projectConfig) {
        Document projectConfigDoc = Document.parse(JSON.toJSONString(projectConfig));
        scoreDatabase.getCollection("project_config").replaceOne(
                doc("projectId", projectConfig.getProjectId()),
                projectConfigDoc, new UpdateOptions().upsert(true)
        );
    }

    public ProjectConfig getProjectConfig(String projectId) {
        String cacheKey = "project_config:" + projectId;

        return cache.get(cacheKey, () -> {
            Document document = scoreDatabase.getCollection("project_config")
                    .find(doc("projectId", projectId)).first();

            if (document == null) {
                return projectId.equals("[default]") ? null : getProjectConfig("[default]");
            } else {
                return JSON.toJavaObject(JSON.parseObject(document.toJson()), ProjectConfig.class);
            }
        });
    }
}
