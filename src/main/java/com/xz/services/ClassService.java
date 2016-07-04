package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.DocumentUtils.addTo;
import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * (description)
 * created at 16/05/21
 *
 * @author yiding_he
 */
@Service
public class ClassService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询考试班级名称
     *
     * @param projectId 考试项目id
     * @param classId   班级id
     *
     * @return 学校名称
     */
    public String getClassName(String projectId, String classId) {
        Document examClass = findClass(projectId, classId);
        if (examClass == null) {
            return "";
        }

        String className = examClass.getString("name");
        if (StringUtil.isBlank(className)) {
            return "";
        }

        if (!className.contains("班")) {
            className += "班";
        }

        return className;
    }

    public Document findClass(String projectId, String classId) {
        String cacheKey = "class:" + projectId + ":" + classId;

        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("class_list");
            Document query = doc("project", projectId).append("class", classId);
            return collection.find(query).first();
        });
    }

    /**
     * 查询班级列表
     *
     * @param projectId 考试项目id
     * @param schoolId  学校id
     *
     * @return 班级列表
     */
    public List<Document> listClasses(String projectId, String schoolId) {
        String cacheKey = "class_list:" + projectId + ":" + schoolId;

        return cache.get(cacheKey, () -> {
            ArrayList<Document> classes = new ArrayList<>();

            MongoCollection<Document> collection = scoreDatabase.getCollection("class_list");
            Document query = doc("project", projectId);
            addTo(query, "school", schoolId);

            classes.addAll(toList(collection.find(query).projection(MongoUtils.WITHOUT_INNER_ID)));
            return classes;
        });
    }

    //////////////////////////////////////////////////////////////

    /**
     * 保存单个考试班级记录
     *
     * @param classDoc 考试班级记录
     */
    public void saveClass(Document classDoc) {
        Document update = new Document(classDoc);

        Document query = new Document()
                .append("project", update.remove("project"))
                .append("class", update.remove("class"));

        scoreDatabase.getCollection("class_list").updateOne(query, $set(update), UPSERT);
    }

    /**
     * 保存一个学校的考试班级记录
     *
     * @param projectId 考试项目ID
     * @param schoolId  学校ID
     * @param classes   考试班级记录列表
     */
    public void saveProjectSchoolClasses(String projectId, String schoolId, List<Document> classes) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("class_list");
        collection.deleteMany(doc("project", projectId).append("school", schoolId));
        collection.insertMany(classes);
    }
}
