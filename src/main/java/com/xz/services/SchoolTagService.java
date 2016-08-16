package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * @author by fengye on 2016/8/15.
 */
@Service
public class SchoolTagService {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 根据标签的多个key查询标签
     */
    public List<Document> findTagsByKeys(String... key) {
        String cacheKey = "school_tags:" + "list:" + key;
        return cache.get(cacheKey, () -> {
            ArrayList<Document> result = new ArrayList<>();
            MongoCollection<Document> school_tags = scoreDatabase.getCollection("school_tags");
            result.addAll(toList(school_tags.find(doc("key", $in(key))).projection(WITHOUT_INNER_ID)));
            return result;
        });
    }

    /**
     * 根据单个key查询标签
     */
    public Document findOneTagByKey(String key) {
        String cacheKey = "school_tags:" + key;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> school_tags = scoreDatabase.getCollection("school_tags");
            return school_tags.find(doc("key", key)).projection(WITHOUT_INNER_ID).first();
        });
    }

    /**
     * 根据单个key查询标签名称
     */
    public String findTagNameByKey(String key) {
        Document tag = findOneTagByKey(key);
        if (null == tag) {
            return "";
        }
        return tag.getString("value");
    }

}
