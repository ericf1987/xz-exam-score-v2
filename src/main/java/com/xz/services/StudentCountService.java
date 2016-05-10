package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Service
public class StudentCountService {

    @Autowired
    SimpleCache simpleCache;

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询项目考生数量
     *
     * @param projectId 项目ID
     * @param range     范围
     *
     * @return 考生数量
     */
    public int getStudentCount(String projectId, Range range) {
        return getStudentCount(projectId, "000", range);
    }

    /**
     * 查询科目考生数量
     *
     * @param projectId 项目ID
     * @param subjectId 科目ID
     * @param range     范围
     *
     * @return 考生数量
     */
    public int getStudentCount(String projectId, String subjectId, Range range) {
        String cacheKey = "student_count:" + projectId;

        return simpleCache.get(cacheKey, () -> {

            Document match = new Document("$match", new Document("projectId", projectId)
                    .append("subjectId", subjectId).append(range.getName(), range.getId()));

            Document group = new Document("$group", new Document("_id", null)
                    .append("sum", new Document("$sum", "$studentCount")));

            AggregateIterable<Document> iterable = scoreDatabase
                    .getCollection("student_count")
                    .aggregate(Arrays.asList(match, group));

            Iterator<Document> iterator = iterable.iterator();
            if (iterator.hasNext()) {
                return ((Number) iterator.next().get("sum")).intValue();
            } else {
                return 0;
            }
        });
    }
}
