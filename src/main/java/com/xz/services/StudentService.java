package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.WITHOUT_INNER_ID;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.SubjectUtil.isCombinedSubject;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Service
public class StudentService {

    static final Logger LOG = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    SimpleCache simpleCache;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    TargetService targetService;

    @Autowired
    ProjectConfigService projectConfigService;

    /**
     * 查询项目考生数量
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return 考生数量
     */
    public int getStudentCount(String projectId, Range range, Target target) {
        if (target.match(Target.PROJECT)) {
            return getStudentCount(projectId, range);
        } else {
            String subjectId = targetService.getTargetSubjectId(projectId, target);
            return getStudentCount(projectId, subjectId, range);
        }
    }

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
        String cacheKey = getCacheKey("student_count:", projectId, subjectId, range);

        return simpleCache.get(cacheKey, () -> {
            Document query = new Document("project", projectId).append(range.getName(), range.getId());
            if (subjectId != null) {
                query.append("subjects", subjectId);
            }
            int studentCount = (int) scoreDatabase.getCollection("student_list").count(query);
            if (studentCount == 0) {
                LOG.error("找不到学生数量: " + query.toJson());
            }
            return studentCount;
        });
    }

    private String getCacheKey(String prefix, String projectId, String subjectId, Range range) {
        String cacheKey = prefix + projectId;
        if (subjectId != null) {
            cacheKey += ":" + subjectId;
        }
        cacheKey += ":" + range.getName() + ":" + range.getId();
        return cacheKey;
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
        String subjectId = targetService.getTargetSubjectId(projectId, target);
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

        final Value<String> subjectIdValue = Value.of(subjectId);  // needs to be final
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

        // 如果查询文理合并科目的学生列表，则将 subjectId 置为空，这样将返回 project 的学生列表
        if (projectConfig.isCombineCategorySubjects() && isCombinedSubject(subjectId)) {
            subjectIdValue.set(null);
        }

        String cacheKey = getCacheKey("student_list:", projectId, subjectIdValue.get(), range);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> students = scoreDatabase.getCollection("student_list");
            ArrayList<String> studentIds = new ArrayList<>();
            Document query = new Document("project", projectId).append(range.getName(), range.getId());

            if (subjectIdValue.get() != null) {
                query.append("subjects", subjectIdValue.get());
            }

            FindIterable<Document> studentLists = students.find(query).projection(doc("student", 1));
            studentLists.forEach((Consumer<Document>) doc -> studentIds.add(doc.getString("student")));

            return studentIds;
        });
    }

    /**
     * 查询学生列表
     *
     * @param projectId 项目ID
     * @param range     范围
     *
     * @return 学生ID列表
     */
    public List<Document> getStudentList(String projectId, Range range) {
        String cacheKey = "student_list_range:" + projectId + ":" + range;

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("student_list");
            Document query = new Document("project", projectId).append(range.getName(), range.getId());

            FindIterable<Document> studentLists = collection.find(query)
                    .projection(WITHOUT_INNER_ID.append("subjects", 0));
            return new ArrayList<>(MongoUtils.toList(studentLists));
        });
    }

    /**
     * 查询学生所属的班级、学校、省市区ID
     *
     * @param studentId 学生ID
     * @param rangeName 范围类型，例如 Range.SCHOOL
     */
    public Range getStudentRange(String projectId, String studentId, String rangeName) {
        Document studentDoc = findStudent(projectId, studentId);
        if (studentDoc == null) {
            throw new IllegalArgumentException("找不到考生, project=" + projectId + ", student=" + studentId);
        } else {
            return new Range(rangeName, studentDoc.getString(rangeName));
        }
    }

    public Document findStudent(String projectId, String studentId) {
        String cacheKey = "student:" + projectId + ":" + studentId;

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> students = scoreDatabase.getCollection("student_list");
            return students.find(doc("student", studentId).append("project", projectId)).first();
        });
    }
}
