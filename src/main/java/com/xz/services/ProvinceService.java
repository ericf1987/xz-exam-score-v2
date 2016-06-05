package com.xz.services;

import com.mongodb.client.MongoDatabase;
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
        scoreDatabase.getCollection("province_list").updateOne(
                doc("project", projectId), $set("province", province), UPSERT);
    }
}
