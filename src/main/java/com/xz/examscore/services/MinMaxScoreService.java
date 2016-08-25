package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinMaxScoreService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询最低分最高分
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return 最低分、最高分
     */
    public double[] getMinMaxScore(String projectId, Range range, Target target) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("score_minmax");

        Document query = Mongo.query(projectId, range, target);
        Document document = collection.find(query).first();
        if (document != null) {
            return new double[]{document.getDouble("min"), document.getDouble("max")};
        }

        return new double[]{0, 0};
    }
}
