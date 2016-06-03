package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.beans.exam.ExamProject;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * 查询指定学校的考试项目
     *
     * @param schoolId  学校id
     * @param examMonth 考试月份 格式 yyyy-MM
     *
     * @return  考试项目列表
     */
    public List<Document> querySchoolProjects(String schoolId, String examMonth) {
        String cacheKey = "school_projects:" + schoolId + ":" + examMonth;
        return cache.get(cacheKey, () -> {
            Document query = doc("schools.school", schoolId);
            Document projection = MongoUtils.WITHOUT_INNER_ID.append("schools", 0);

            if (StringUtil.isNotBlank(examMonth)) {
                Pattern like = Pattern.compile("^" + examMonth);
                query.append("importDate", doc("$regex", like));
            }

            return new ArrayList<>(toList(scoreDatabase.getCollection("project_list")
                    .find(query).projection(projection).sort(doc("importDate", -1))));
        });
    }

    /**
     * 保存项目信息（不包含学校列表）
     *
     * @param project 项目信息
     */
    public void saveProject(ExamProject project) {
        MongoCollection<Document> c = scoreDatabase.getCollection("project_list");
        Document query = doc("project", project.getId());
        Document update = doc("name", project.getName()).append("grade", project.getGrade());
        c.updateOne(query, $set(update), UPSERT);
    }

    /**
     * 保存学校列表
     *
     * @param projectId  项目ID
     * @param schoolList 学校列表（name 和 school 属性）
     */
    public void saveProjectSchools(String projectId, List<Document> schoolList) {
        MongoCollection<Document> c = scoreDatabase.getCollection("project_list");
        Document query = doc("project", projectId);
        c.updateOne(query, $set("schools", schoolList));
    }
}
