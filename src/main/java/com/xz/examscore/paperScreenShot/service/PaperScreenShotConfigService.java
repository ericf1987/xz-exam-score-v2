package com.xz.examscore.paperScreenShot.service;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.appauth.AppAuthClient;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * @author by fengye on 2017/5/3.
 */
@Service
public class PaperScreenShotConfigService {

    @Autowired
    AppAuthClient appAuthClient;

    @Autowired
    private MongoDatabase scoreDatabase;


    public Map<String, Object> getConfigFromCMS(String projectId) {
        Result result = appAuthClient.callApi("QueryProjectConfig",
                new Param().setParameter("projectId", projectId)
                        .setParameter("settingKey", "paperBuild"));
        return result.getData();
    }

    public Document pssConfig2Doc(String projectId) {
        Document doc = new Document();
        Map<String, Object> configMap = getConfigFromCMS(projectId);
        doc.putAll(configMap);
        return doc;
    }

    public void saveConfig(String projectId, Document document){
        if(null != document && !document.isEmpty()){
            removeConfig(projectId);
            document.append("project", projectId);
            scoreDatabase.getCollection("paperScreenShot_config")
                    .insertOne(document.append("md5", MD5.digest(UUID.randomUUID().toString())));
        }
    }

    public void removeConfig(String projectId){
        scoreDatabase.getCollection("paperScreenShot_config").deleteMany(
                MongoUtils.doc("project", projectId)
        );
    }

    public Document getConfig(String projectId){
        return scoreDatabase.getCollection("paperScreenShot_config").find(
                MongoUtils.doc("project", projectId)
        ).projection(MongoUtils.doc("project", 0)).first();
    }

}
