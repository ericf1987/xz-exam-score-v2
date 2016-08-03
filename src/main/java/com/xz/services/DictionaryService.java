package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.WITHOUT_INNER_ID;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;

/**
 * @author by fengye on 2016/7/28.
 */
@Service
public class DictionaryService {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 根据字典类型查询所有字典项
     * @param type 字典类型
     * @return
     */
    public List<Document> listItems(String type) {
        String cacheKey = "dictionaries:" + type;

        return cache.get(cacheKey, () -> {

            MongoCollection<Document> c = scoreDatabase.getCollection("dictionaries");
            ArrayList<Document> items = new ArrayList<>();
            Document query = doc("type", type);
            items.addAll(toList(c.find(query).sort(doc("key", 1)).projection(WITHOUT_INNER_ID)));

            return items;
        });

    }

    /**
     * 根据字典类型和字典键查询字典
     * @param type 字典类型
     * @param key 字典键
     * @return
     */
    public Document findDictionary(String type, String key){
        String cacheKey = "dictionaries:" + type + ":" + key;

        return cache.get(cacheKey, () -> {
            MongoCollection<Document> c = scoreDatabase.getCollection("dictionaries");
            Document query = doc("type", type).append("key", key);
            return c.find(query).first();
        });
    }

}
