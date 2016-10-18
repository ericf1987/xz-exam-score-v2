package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2016/10/18.
 */
@Service
public class SubjectCombinationService {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    @Autowired
    ImportProjectService importProjectService;

    //获取所有科目组合
    public ArrayList<String> getAllSubjectCombinations(String projectId) {
        String cacheKey = "subject_combination_list:" + projectId;
        return new ArrayList<>(cache.get(cacheKey, () -> {
            ArrayList<String> targets = new ArrayList<>();
            MongoCollection<Document> collection = scoreDatabase.getCollection("subject_combination_list");
            collection.find(doc("project", projectId)).forEach((Consumer<Document>) document -> {
                List<String> subjectCombinationId = (List<String>) document.get("subject_combinations");
                targets.addAll(subjectCombinationId);
            });
            return targets;
        }));
    }

    //获取组合科目名称
    public String getSubjectCombinationName(String subjectCombinationId) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < subjectCombinationId.length(); i += ImportProjectService.SUBJECT_LENGTH) {
            String subjectId = subjectCombinationId.substring(i, i + ImportProjectService.SUBJECT_LENGTH);
            //取每一科目的首汉字组合
            builder.append(SubjectService.getSubjectName(subjectId).substring(0, 1));
        }
        return builder.toString();
    }

    public void saveProjectSubjectCombinations(String projectId, List<String> subject_combinations) {
        MongoCollection<Document> c = scoreDatabase.getCollection("subject_combination_list");
        Document query = doc("project", projectId);
        UpdateResult result = c.updateMany(query, $set(doc("subject_combinations", subject_combinations)));
        if(result.getMatchedCount()== 0){
            c.insertOne(
                    query.append("subject_combinations", subject_combinations)
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }
}
