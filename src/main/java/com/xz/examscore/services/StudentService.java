package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.concurrent.LockFactory;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.SubjectUtil.isCombinedSubject;

/**
 * 查询学生列表
 * <p>
 * 注意，在 student_list 记录中，subjects 属性如果不存在，表示该考生没有参加任何考试，不应计入统计当中。
 *
 * @author yiding_he
 */
@Service
public class StudentService {

    static final Logger LOG = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    SimpleCache cache;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    TargetService targetService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ImportProjectService importProjectService;

    /**
     * 查询项目考生数量
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     * @return 考生数量
     */
    public int getStudentCount(String projectId, Range range, Target target) {
        if (target.match(Target.PROJECT)) {
            return getStudentCount(projectId, range);
        } else if (target.match(Target.SUBJECT_COMBINATION)) {
            //如果是组合科目，取参考了其中任何一科的参考人数
            return getStudentIds(projectId, range, target).size();
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
     * @return 考生数量
     */
    public int getStudentCount(String projectId, String subjectId, Range range) {
        String cacheKey = getCacheKey("student_count:", projectId, subjectId, range);

        return cache.get(cacheKey, () -> {
            Document query = new Document("project", projectId).append(range.getName(), range.getId());
            if (subjectId != null) {
                query.append("subjects", subjectId);
            } else {
                query.append("subjects", doc("$exists", true));
            }
            return (int) scoreDatabase.getCollection("student_list").count(query);
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
     * 查询学生列表（非缓存）
     *
     * @param projectId       项目ID
     * @param range           范围（可选，null 表示整个项目）
     * @param maxStudentCount 最多查询多少个学生（调试用，<=0 表示不限）
     * @param skipCount       跳过多少条记录（分页用）
     * @param projection      要取哪些字段（可选，null 表示取所有字段）
     * @return 学生列表
     */
    public FindIterable<Document> getProjectStudentList(
            String projectId, Range range, int maxStudentCount, int skipCount, Document projection) {

        MongoCollection<Document> students = scoreDatabase.getCollection("student_list");

        Document query = doc("project", projectId);
        if (range != null) {
            query.append(range.getName(), range.getId());
        }

        FindIterable<Document> findIterable = students.find(query);

        if (maxStudentCount > 0) {
            findIterable.limit(maxStudentCount);
        }

        if (skipCount > 0) {
            findIterable.skip(skipCount);
        }

        if (projection != null) {
            findIterable.projection(projection);
        }

        return findIterable;
    }

    /**
     * 查询学生列表
     *
     * @param projectId 项目ID
     * @param target    目标
     * @param range     范围
     * @return 学生ID列表
     */
    public List<String> getStudentIds(String projectId, Range range, Target target) {
        String cacheKey = "student_id_list:" + projectId + ":" + range + ":" + target;
        //如果是科目组合，则统计至少包含参与了三科中其中至少一科的学生的人数
        if (target.getName().equals(Target.SUBJECT_COMBINATION)) {
            return cache.get(cacheKey, () -> {
                List<String> subjectIds = importProjectService.separateSubject(target.getId().toString());
                List<String> studentIds = new ArrayList<>();
                for (String subjectId : subjectIds) {
                    getStudentIds(projectId, range, Target.subject(subjectId)).forEach(id -> {
                        if (!studentIds.contains(id))
                            studentIds.add(id);
                    });
                }
                return (ArrayList<String>) studentIds;
            });
        } else {
            return cache.get(cacheKey, () -> {
                String subjectId = targetService.getTargetSubjectId(projectId, target);
                return (ArrayList<String>) getStudentIds(projectId, subjectId, range);
            });
        }
    }

    /**
     * 查询学生列表
     *
     * @param projectId 项目ID
     * @param subjectId 科目ID（可选，null表示不论科目）
     * @param range     范围（可选，null表示所有参考学生）
     * @return 学生ID列表
     */
    public List<String> getStudentIds(String projectId, String subjectId, Range range) {

        final Value<String> subjectIdValue = Value.of(subjectId);  // needs to be final
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

        // 如果查询文理合并科目的学生列表，则将 subjectId 置为空，这样将返回 project 的学生列表
        if (projectConfig.isCombineCategorySubjects() && isCombinedSubject(subjectId)) {
            subjectIdValue.set(null);
        }

        String cacheKey = getCacheKey("student_list:", projectId, subjectIdValue.get(), range);

        synchronized (LockFactory.getLock(cacheKey)) {
            return cache.get(cacheKey, () -> {
                MongoCollection<Document> students = scoreDatabase.getCollection("student_list");
                ArrayList<String> studentIds = new ArrayList<>();
                Document query = new Document("project", projectId);
                if (range != null) {
                    query.append(range.getName(), range.getId());
                }

                if (subjectIdValue.get() != null) {
                    query.append("subjects", subjectIdValue.get());
                } else {
                    query.append("subjects", doc("$exists", true));
                }

                FindIterable<Document> studentLists = students.find(query).projection(doc("student", 1));
                studentLists.forEach((Consumer<Document>) doc -> studentIds.add(doc.getString("student")));

                return studentIds;
            });
        }
    }

    /**
     * 查询学生列表
     *
     * @param projectId 项目ID
     * @param range     范围
     * @return 学生ID列表
     */
    public List<Document> getStudentList(String projectId, Range range) {
        String cacheKey = "student_list_range:" + projectId + ":" + range;

        return cache.get(cacheKey, () -> {
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

        return cache.get(cacheKey, () -> {
            MongoCollection<Document> students = scoreDatabase.getCollection("student_list");
            return students.find(doc("student", studentId).append("project", projectId)).first();
        });
    }

    public void saveProjectClassStudents(String projectId, String classId, List<Document> classStudents) {
        MongoCollection<Document> students = scoreDatabase.getCollection("student_list");

        students.deleteMany(doc("project", projectId).append("class", classId));
        students.insertMany(classStudents);

        String cacheKey = "student_list_range:" + projectId + ":" + Range.clazz(classId);
        cache.delete(cacheKey);
    }

    public ArrayList<Document> pickStudentsByRange(String projectId, List<String> studentIds, String rangeName) {
        String cacheKey = "studentInRange:" + rangeName + ":" + studentIds;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> students = scoreDatabase.getCollection("student_list");
            Document match = doc("project", projectId);
            if(null != studentIds && !studentIds.isEmpty()){
                match.append("student", doc("$in", studentIds));
            }
            Document group = doc("_id", "$" + rangeName).append("students", $addToSet("$student"));
            AggregateIterable<Document> aggregate = students.aggregate(Arrays.asList(
                    $match(match), $group(group)
            ));
            return new ArrayList<>(toList(aggregate));
        });
    }
}
