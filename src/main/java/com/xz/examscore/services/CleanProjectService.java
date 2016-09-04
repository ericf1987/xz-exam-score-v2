package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/9/3.
 */
@Service
public class CleanProjectService {

    @Autowired
    public static final Logger LOG = LoggerFactory.getLogger(CleanProjectService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    public void doCleanSchedule(String projectId) {
        List<String> collections = getAllCollections();
        LOG.warn("----------当前考试ID为：{}，开始执行数据清理----------", projectId);
        collections.forEach(collection -> doClean(collection, projectId));
        LOG.warn("----------当前考试ID为：{}，执行完成----------",projectId);
    }

    private void doClean(String collection, String projectId) {
        LOG.warn("开始执行集合{}的数据清理", collection);
        Long begin = System.currentTimeMillis();
        try {
            MongoCollection<Document> document = scoreDatabase.getCollection(collection);
            Document query = new Document();
            if(collection.equals("project_config")){
                query.append("projectId", projectId);
            }else{
                query.append("project", projectId);
            }
            document.deleteMany(query);
        } catch (Exception e) {
            LOG.warn("执行操作失败，集合名称为：{}", collection);
        } finally {
            Long end = System.currentTimeMillis();
            LOG.warn("集合{}的数据清理执行完成, 总耗时为：{}", collection, end - begin);
        }
    }

    //获取数据库所有集合
    public List<String> getAllCollections() {
        List<String> collections = new ArrayList();
        MongoIterable<String> collectionNames = scoreDatabase.listCollectionNames();
        for (String name : collectionNames) {
            collections.add(name);
        }
        return collections;
    }
}
