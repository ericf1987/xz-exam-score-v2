package com.xz.examscore.services;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * (description)
 * created at 16/06/05
 *
 * @author yiding_he
 */
@Service
public class ProvinceService {

    @Autowired
    MongoDatabase scoreDatabase;

    public void saveProjectProvince(String projectId, String province) {

        if (province == null) {
            return;
        }

        UpdateResult result = scoreDatabase.getCollection("province_list").updateMany(
                doc("project", projectId), $set(doc("province", province))
        );
        if (result.getMatchedCount() == 0) {
            scoreDatabase.getCollection("province_list").insertOne(
                    doc("project", projectId).append("province", province)
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    public String getProjectProvince(String projectId) {
        Document document = scoreDatabase.getCollection("province_list").find(doc("project", projectId)).first();
        return document == null ? null : document.getString("province");
    }
}
