package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Service
public class StudentService {

    @Autowired
    SimpleCache simpleCache;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    TargetService targetService;

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

    /**
     * 查询学生列表
     *
     * @param projectId 项目ID
     * @param target    目标
     * @param range     范围
     *
     * @return 学生ID列表
     */
    public List<String> getStudentList(String projectId, Range range, Target target) {
        String subjectId = targetService.getTargetSubjectId(target);
        return getStudentList(projectId, subjectId, range);
    }

    /**
     * 查询学生列表
     *
     * @param projectId 项目ID
     * @param subjectId 科目ID
     * @param range     范围
     *
     * @return 学生ID列表
     */
    public List<String> getStudentList(String projectId, String subjectId, Range range) {
        MongoCollection<Document> students = scoreDatabase.getCollection("student_list");

        List<String> studentIds = new ArrayList<>();

        FindIterable<Document> studentLists = students.find(
                new Document("project", projectId)
                        .append("subjects", subjectId)
                        .append(range.getName(), range.getId())
        );

        studentLists.forEach((Consumer<Document>) doc -> {
            studentIds.add(doc.getString("student"));
        });
        return studentIds;
    }

}
