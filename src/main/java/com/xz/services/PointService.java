package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Point;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 知识点与能力层级
 *
 * @author yiding_he
 */
@Service
public class PointService {

    static final Logger LOG = LoggerFactory.getLogger(PointService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询指定项目的指定科目的知识点列表
     *
     * @param projectId 项目ID
     * @param subjectId 科目ID
     *
     * @return 知识点列表
     */
    @SuppressWarnings("unchecked")
    public List<Point> getPoints(String projectId, String subjectId) {
        String cacheKey = "points:" + projectId + ":" + subjectId;
        String mapFunction = "for (var key in this.points) {emit(key, null);}";
        String reduceFunction = "return null;";

        return cache.get(cacheKey, () -> {
            Document result = scoreDatabase.runCommand(
                    doc("mapreduce", "quest_list")
                            .append("query", doc("project", projectId).append("subject", subjectId))
                            .append("map", mapFunction)
                            .append("reduce", reduceFunction)
                            .append("out", doc("inline", 1))
            );

            List<Document> pointDocs = (List<Document>) result.get("results");
            ArrayList<Point> points = new ArrayList<>();

            for (Document pointDoc : pointDocs) {
                String pointId = pointDoc.getString("_id");
                points.add(getPoint(pointId));
            }

            return points;
        });
    }

    public Point getPoint(String pointId) {
        String cacheKey = "point:" + pointId;
        return cache.get(cacheKey, () -> {
            Document document = scoreDatabase.getCollection("points").find(doc("id", pointId)).first();
            if (document == null) {
                return new Point(pointId, "(知识点" + pointId + ")");
            } else {
                return new Point(pointId, document.getString("name"));
            }
        });
    }

    public boolean exists(String pointId) {
        return scoreDatabase.getCollection("points").count(doc("id", pointId)) > 0;
    }

    public void savePoint(String pointId, String pointName) {
        scoreDatabase.getCollection("points").updateOne(doc("id", pointId), $set("name", pointName), UPSERT);
    }
}
