package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.beans.exam.ExamProject;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.bean.ProjectStatus;
import com.xz.examscore.bean.Range;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 考试项目业务类
 *
 * @author zhaorenwu
 */
@Service
public class ProjectService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    private static final Map<String, String> GRADE_STUDYSTAGE_MAP = new HashMap<>();

    @PostConstruct
    public void init() {
        GRADE_STUDYSTAGE_MAP.put("1", "1");
        GRADE_STUDYSTAGE_MAP.put("2", "1");
        GRADE_STUDYSTAGE_MAP.put("3", "1");
        GRADE_STUDYSTAGE_MAP.put("4", "1");
        GRADE_STUDYSTAGE_MAP.put("5", "1");
        GRADE_STUDYSTAGE_MAP.put("6", "1");

        GRADE_STUDYSTAGE_MAP.put("7", "2");
        GRADE_STUDYSTAGE_MAP.put("8", "2");
        GRADE_STUDYSTAGE_MAP.put("9", "2");

        GRADE_STUDYSTAGE_MAP.put("10", "3");
        GRADE_STUDYSTAGE_MAP.put("11", "3");
        GRADE_STUDYSTAGE_MAP.put("12", "3");

        GRADE_STUDYSTAGE_MAP.put("0", "0");
    }

    /**
     * 通过考试项目id查询项目所属学段
     *
     * @param projectId 考试项目id
     * @return 考试项目信息
     */
    public String findProjectStudyStage(String projectId) {
        String cacheKey = "project_studystage:" + projectId;
        return cache.get(cacheKey, () -> {
            Document projectInfo = findProject(projectId);

            int grade = 0;
            if (projectInfo != null) {
                grade = DocumentUtils.getInt(projectInfo, "grade", 0);
            }

            return GRADE_STUDYSTAGE_MAP.get(String.valueOf(grade));
        });
    }

    /**
     * 通过考试项目id查询考试项目
     *
     * @param projectId 考试项目id
     * @return 考试项目信息
     */
    public Document findProject(String projectId) {
        String cacheKey = "project_info:" + projectId;
        return cache.get(cacheKey, () -> {
            Document query = doc("project", projectId);

            MongoCollection<Document> collection = scoreDatabase.getCollection("project_list");
            return collection.find(query).projection(WITHOUT_INNER_ID.append("schools", 0)).first();
        });
    }

    /**
     * 查询指定学校的考试项目
     *
     * @param city      地市id
     * @param area      区县id
     * @param schoolId  学校id
     * @param examMonth 考试月份 格式 yyyy-MM
     * @return 考试项目列表
     */
    public List<Document> querySchoolProjects(String city, String area, String schoolId, String examMonth) {
        // 此处不用缓存加载以免无法及时刷新变更
        Document query = new Document();
        if (StringUtil.isNotBlank(schoolId)) {
            query.put("schools.school", schoolId);
        } else if (StringUtil.isNotBlank(area)) {
            query.put("schools.area", area);
        } else {
            query.put("schools.city", city);
        }

        Document projection = MongoUtils.WITHOUT_INNER_ID.append("schools", 0);

        if (StringUtil.isNotBlank(examMonth)) {
            Pattern like = Pattern.compile("^" + examMonth);
            query.append("importDate", doc("$regex", like));
        }

        return toList(scoreDatabase.getCollection("project_list")
                .find(query).projection(projection).sort(doc("importDate", -1)));
    }

    /**
     * 保存项目信息（不包含学校列表）
     *
     * @param project 项目信息
     */
    public void saveProject(ExamProject project) {
        MongoCollection<Document> c = scoreDatabase.getCollection("project_list");
        Document query = doc("project", project.getId());

        Document update = doc("name", project.getName())
                .append("grade", project.getGrade())
                .append("importDate", DateFormatUtils.format(project.getCreateTime(), "yyyy-MM-dd"))
                .append("startDate", project.getExamStartDate())
                .append("category", project.getCategory());

        UpdateResult result = c.updateMany(query, $set(update));
        if (result.getMatchedCount() == 0) {
            c.insertOne(query.append("name", project.getName())
                    .append("grade", project.getGrade())
                    .append("importDate", DateFormatUtils.format(project.getCreateTime(), "yyyy-MM-dd"))
                    .append("startDate", project.getExamStartDate())
                    .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    /**
     * 更新项目学校列表（如果项目记录未创建则不做任何操作）
     *
     * @param projectId  项目ID
     * @param schoolList 学校列表（name 和 school 属性）
     */
    public void updateProjectSchools(String projectId, List<Document> schoolList) {
        MongoCollection<Document> c = scoreDatabase.getCollection("project_list");
        Document query = doc("project", projectId);
        c.updateMany(query, $set("schools", schoolList));
    }

    /**
     * 更新项目统计时间
     */
    public void updateAggregationTime(String projectId) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(Calendar.getInstance().getTime());
        MongoCollection<Document> c = scoreDatabase.getCollection("project_list");
        Document query = doc("project", projectId);
        c.updateMany(query, $set("aggregationTime", result));
    }

    /**
     * 查询所有项目信息
     *
     * @return 所有项目
     */
    public List<Document> queryProjects() {
        return toList(scoreDatabase.getCollection("project_list").find(doc()));
    }

    /**
     * 设置项目状态
     *
     * @param projectId 项目ID
     * @param status    状态
     */
    public void setProjectStatus(String projectId, ProjectStatus status) {
        MongoCollection<Document> c = scoreDatabase.getCollection("project_list");
        Document query = doc("project", projectId);
        c.updateMany(query, $set("status", status.name()));

        // 清除缓存
        String cacheKey = "project_info:" + projectId;
        cache.delete(cacheKey);
    }

    /**
     * 查询项目状态
     *
     * @param projectId 项目ID
     * @return 状态
     */
    public ProjectStatus getProjectStatus(String projectId) {
        MongoCollection<Document> c = scoreDatabase.getCollection("project_list");
        Document query = doc("project", projectId);
        Document project = c.find(query).projection(doc("status", 1)).first();
        return project == null ? ProjectStatus.Empty : ProjectStatus.valueOf(project.getString("status"));
    }

    /**
     * 查询所有项目ID列表
     *
     * @return 所有项目ID列表
     */
    public List<String> listProjectIds() {
        List<String> result = new ArrayList<>();

        scoreDatabase.getCollection("project_list")
                .find(doc()).projection(doc("project", 1))
                .forEach((Consumer<Document>)
                        document -> result.add(document.getString("project")));

        return result;
    }

    /**
     * 根据range查看对应的所有考试信息
     */
    public List<Document> listProjectsByRange(Range range, String category) {
        String collectionName = range.getName() + "_list";
        Document projection = MongoUtils.doc("project", 1);

        Document query = doc(range.getName(), range.getId());

        if(!StringUtils.isEmpty(category)){
            query.append("category", category);
        }

        List<Document> projectDoc = toList(scoreDatabase.getCollection(collectionName)
                .find(query).projection(projection));

        List<Document> projects = new ArrayList<>();
        for (Document doc : projectDoc) {
            String projectId = doc.getString("project");
            Document d = findProject(projectId);
            projects.add(d);
        }

        return projects;
    }
}
