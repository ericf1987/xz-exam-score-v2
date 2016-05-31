package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
@Service
public class SubjectService {

    private static final Map<String, String> SUBJECT_NAMES = new HashMap<>();

    static {
        SUBJECT_NAMES.put("001", "语文");
        SUBJECT_NAMES.put("002", "数学");
        SUBJECT_NAMES.put("003", "英语");
        SUBJECT_NAMES.put("004", "物理");
        SUBJECT_NAMES.put("005", "化学");
        SUBJECT_NAMES.put("006", "生物");
        SUBJECT_NAMES.put("007", "政治");
        SUBJECT_NAMES.put("008", "历史");
        SUBJECT_NAMES.put("009", "地理");
        SUBJECT_NAMES.put("010", "社会");
        SUBJECT_NAMES.put("011", "科学");
        SUBJECT_NAMES.put("012", "技术与设计");
        SUBJECT_NAMES.put("013", "思想品德");
        SUBJECT_NAMES.put("014", "信息技术");
        SUBJECT_NAMES.put("004005006", "理科综合");
        SUBJECT_NAMES.put("007008009", "文科综合");
    }

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache simpleCache;

    /**
     * 查询考试项目的科目列表
     *
     * @param projectId 项目ID
     *
     * @return 科目ID列表
     */
    @SuppressWarnings("unchecked")
    public List<String> querySubjects(String projectId) {
        String cacheKey = "subject_list:" + projectId;

        return simpleCache.get(cacheKey, () -> {
            ArrayList<String> targets = new ArrayList<>();
            MongoCollection<Document> collection = scoreDatabase.getCollection("subject_list");

            collection.find(doc("project", projectId)).forEach((Consumer<Document>) document -> {
                List<String> subjectIds = (List<String>) document.get("subjects");
                targets.addAll(subjectIds);
            });

            return targets;
        });
    }

    public static String getSubjectName(String subjectId) {
        return SUBJECT_NAMES.get(subjectId);
    }
}
