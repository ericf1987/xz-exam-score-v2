package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * (description)
 * created at 16/05/24
 *
 * @author yiding_he
 */
@Service
public class SchoolService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectCacheManager projectCacheManager;

    /**
     * 查询考试学校名称
     *
     * @param projectId 考试项目id
     * @param schoolId  学校id
     *
     * @return 学校名称
     */
    public String getSchoolName(String projectId, String schoolId) {
        Document examSchool = findSchool(projectId, schoolId);
        if (examSchool == null) {
            return "";
        }

        return examSchool.getString("name");
    }

    /**
     * 查询指定考试学校
     *
     * @param projectId 考试项目id
     * @param schoolId  学校id
     *
     * @return 考试学校
     */
    public Document findSchool(String projectId, String schoolId) {
        String cacheKey = "school_info:" + projectId + ":" + schoolId;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {

            Document query = doc("project", projectId).append("school", schoolId);
            return scoreDatabase.getCollection("school_list").find(query).projection(WITHOUT_INNER_ID).first();
        });
    }

    /**
     * 查询考试学校id列表
     *
     * @param projectId 考试项目id
     * @param area      地区编码
     *
     * @return 考试学校列表
     */
    public List<String> getProjectSchoolIds(String projectId, String area) {
        String cacheKey = "school_id_list:" + projectId + ":" + area;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return new ArrayList<>(simpleCache.get(cacheKey, () ->
                new ArrayList<>(getProjectSchools(projectId, area).stream().map(
                        document -> document.getString("school")).collect(Collectors.toList()))));
    }

    /**
     * 查询考试学校列表
     *
     * @param projectId 考试项目id
     * @param area      地区编码
     *
     * @return 考试学校列表
     */
    public List<Document> getProjectSchools(String projectId, String area) {
        String cacheKey = "school_list:" + projectId + ":" + area;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return new ArrayList<>(simpleCache.get(cacheKey, () -> {
            ArrayList<Document> result = new ArrayList<>();
            List<Document> ands = new ArrayList<>();

            ands.add(doc("project", projectId));
            if (StringUtil.isNotBlank(area)) {
                ands.add($or(doc("province", area), doc("city", area), doc("area", area)));
            }

            result.addAll(toList(scoreDatabase.getCollection("school_list")
                    .find($and(ands)).projection(WITHOUT_INNER_ID)));
            return result;
        }));
    }

    /**
     * 查询考试学校列表
     *
     * @param projectId 考试项目id
     *
     * @return 考试学校列表
     */
    public List<Document> getProjectSchools(String projectId) {
        return getProjectSchools(projectId, "");
    }

    //////////////////////////////////////////////////////////////

    /**
     * 保存项目学校列表（会删除旧数据）
     *
     * @param projectId  项目ID
     * @param schoolList 学校列表
     */
    public void saveProjectSchool(String projectId, List<Document> schoolList) {
        MongoCollection<Document> c = scoreDatabase.getCollection("school_list");
        c.deleteMany(doc("project", projectId));
        if (!schoolList.isEmpty()) {
            c.insertMany(schoolList);
        }
    }

    public void clearProjectSchool(String projectId) {
        MongoCollection<Document> c = scoreDatabase.getCollection("school_list");
        c.deleteMany(doc("project", projectId));
    }

    /**
     * 根据学校标签查询学校列表
     */
    public List<Document> getSchoolsByTags(String projectId, List<String> params) {
        MongoCollection<Document> c = scoreDatabase.getCollection("school_list");
        ArrayList<Document> result = new ArrayList<>();

        Document doc = doc("project", projectId);

        if (null != params && !params.isEmpty())
            doc.append("tags", doc("$all", params));

        result.addAll(toList(c.find(doc).projection(doc("school", 1).append("name", 1).append("tags", 1))));

        return result;
    }

    /**
     * 根据标签分组查询，列出所有对应标签分组对应的学校ID数组
     */
    public List<Document> findSchoolIdsByTags(String projectId) {
        MongoCollection<Document> school_tags = scoreDatabase.getCollection("school_list");
        AggregateIterable<Document> aggregate = school_tags.aggregate(Arrays.asList(
                $match("project", projectId),
                $group(doc("_id", "$tags").append("schoolIds", doc("$push", "$school")))
        ));
        return toList(aggregate);
    }

}
