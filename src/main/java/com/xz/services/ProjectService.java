package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

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
    List<Document> querySchoolProjects(String schoolId, String examMonth) {
        String cacheKey = "school_projects:" + schoolId + ":" + examMonth;
//        return cache.get(cacheKey, () -> {
//            Document query = doc("schools.school", schoolId);
//
//            scoreDatabase.getCollection("project_list").find()
//
//        })

        return new ArrayList<>();
    }
}
