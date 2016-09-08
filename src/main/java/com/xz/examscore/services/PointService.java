package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.bean.Point;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
     * 查询指定项目的所有知识点
     *
     * @param projectId 项目ID
     *
     * @return 知识点列表
     */
    public List<Point> getPoints(String projectId) {
        Document query = doc("project", projectId);
        String cacheKey = "points:" + projectId;
        return getPoints(query, cacheKey);
    }

    /**
     * 查询指定项目的指定科目的知识点列表
     *
     * @param projectId 项目ID
     * @param subjectId 科目ID
     *
     * @return 知识点列表
     */
    public List<Point> getPoints(String projectId, String subjectId) {
        Document query = doc("project", projectId).append("subject", subjectId);
        String cacheKey = "points:" + projectId + ":" + subjectId;
        return getPoints(query, cacheKey);
    }

    @SuppressWarnings("unchecked")
    private List<Point> getPoints(Document query, String cacheKey) {
        String mapFunction = "for (var key in this.points) {emit(key, null);}";
        String reduceFunction = "return null;";

        return new ArrayList<>(cache.get(cacheKey, () -> {
            Document result = scoreDatabase.runCommand(
                    doc("mapreduce", "quest_list")
                            .append("query", query)
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
        }));
    }

    public Point getPoint(String pointId) {
        String cacheKey = "point:" + pointId;
        return cache.get(cacheKey, () -> {
            Document document = scoreDatabase.getCollection("points").find(doc("id", pointId)).first();
            if (document == null) {
                return new Point(pointId, "(知识点" + pointId + ")", document.getString("subject"));
            } else {
                return new Point(pointId, document.getString("name"), document.getString("subject"));
            }
        });
    }

    public boolean exists(String pointId) {
        return scoreDatabase.getCollection("points").count(doc("id", pointId)) > 0;
    }

    public void savePoint(String pointId, String pointName, String parentPointId, String subject) {
        Document query = doc("id", pointId);
        Document update = doc("name", pointName).append("parent", parentPointId).append("subject", subject);
        UpdateResult result = scoreDatabase.getCollection("points").updateMany(query, $set(update));
        if (result.getMatchedCount() == 0) {
            scoreDatabase.getCollection("points").insertOne(
                    query.append("name", pointName).append("subject", subject)
                            .append("parent", parentPointId)
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }
}
