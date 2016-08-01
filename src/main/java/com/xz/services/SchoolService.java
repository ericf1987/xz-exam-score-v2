package com.xz.services;

import com.hyd.appserver.utils.StringUtils;
import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.RandomUtil;
import com.xz.ajiaedu.common.lang.StringUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
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
    SimpleCache cache;

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

        return cache.get(cacheKey, () -> {

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
        return cache.get(cacheKey, () -> {
            return new ArrayList<>(getProjectSchools(projectId, area).stream().map(
                    document -> document.getString("school")).collect(Collectors.toList()));
        });
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

        return cache.get(cacheKey, () -> {
            ArrayList<Document> result = new ArrayList<>();
            List<Document> ands = new ArrayList<>();

            ands.add(doc("project", projectId));
            if (StringUtil.isNotBlank(area)) {
                ands.add($or(doc("province", area), doc("city", area), doc("area", area)));
            }

            result.addAll(toList(scoreDatabase.getCollection("school_list")
                    .find($and(ands)).projection(WITHOUT_INNER_ID)));
            return result;
        });
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
        c.insertMany(schoolList);
    }

    public void clearProjectSchool(String projectId) {
        MongoCollection<Document> c = scoreDatabase.getCollection("school_list");
        c.deleteMany(doc("project", projectId));
    }

    /**
     * 根据学校标签查询学校列表
     */
    public List<Document> getSchoolsByTags(String projectId, String isInCity, String isGovernmental){

        String cacheKey = "school_list:" + projectId + ":" + "isInCity" + ":" + isInCity + "isGovernmental" + ":" + isGovernmental;

        return cache.get(cacheKey, () -> {
            MongoCollection<Document> c = scoreDatabase.getCollection("school_list");
            ArrayList<Document> result = new ArrayList<>();
            List<Document> ands = new ArrayList<>();

            ands.add(doc("project", projectId));

            if (StringUtil.isNotBlank(isInCity)) {
                ands.add(doc("tags", $elemMatch(doc("name", "isInCity").append("value", isInCity))));
            }

            if (StringUtil.isNotBlank(isGovernmental)) {
                ands.add(doc("tags", $elemMatch(doc("name", "isGovernmental").append("value", isGovernmental))));
            }

            result.addAll(toList(c.find($and(ands)).projection(doc("school", 1).append("tags", 1))));

            return result;
        });
    }

    /**
     * 补齐标记
     */
    public void paddingTags(String projectId){
        MongoCollection<Document> c = scoreDatabase.getCollection("school_list");

        c.find(doc("project", projectId)).forEach((Consumer<Document>) document ->{
            String isInCity = getRandomValue();
            String isGovernmental = getRandomValue();
            List<Document> tags = Arrays.asList(doc("name", "isInCity").append("value", isInCity), doc("name", "isGovernmental").append("value", isGovernmental));
            c.updateOne(doc("project", projectId).append("school", document.getString("school")),
                    $set("tags", tags),
                    UPSERT);
        });
    }

    private String getRandomValue(){
        Random r = new Random();
        if(r.nextInt(10) > 7)
            return "true";
        else if (r.nextInt(10) > 3)
            return "false";
        else return "unkown";
    }

}
