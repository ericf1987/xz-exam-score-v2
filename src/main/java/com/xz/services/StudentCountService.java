package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return getStudentCount(projectId, null, range);
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
        if (subjectId != null) {
            cacheKey += ":" + subjectId;
        }

        return simpleCache.get(cacheKey, () -> {
            Document query = new Document("project", projectId).append(range.getName(), range.getId());
            if (subjectId != null) {
                query.append("subjects", subjectId);
            }
            return (int) scoreDatabase.getCollection("student_list").count(query);
        });
    }
}
