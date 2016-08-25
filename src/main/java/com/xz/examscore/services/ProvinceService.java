package com.xz.examscore.services;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        scoreDatabase.getCollection("province_list").updateOne(
                doc("project", projectId), $set("province", province), UPSERT);
    }

    public String getProjectProvince(String projectId) {
        Document document = scoreDatabase.getCollection("province_list").find(doc("project", projectId)).first();
        return document == null ? null : document.getString("province");
    }
}
