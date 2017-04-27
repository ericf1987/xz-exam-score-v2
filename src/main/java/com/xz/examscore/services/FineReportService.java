package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.bean.FineReportItem;
import com.xz.examscore.cache.ProjectCacheManager;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author by fengye on 2017/4/26.
 */
@Service
public class FineReportService {

    @Autowired
    private MongoDatabase scoreDatabase;

    @Autowired
    private ProjectCacheManager projectCacheManager;

    public final void insertItem(FineReportItem item){
        MongoCollection<Document> collection = scoreDatabase.getCollection("fineReport_items");
        Document doc = item2Doc(item);
        collection.insertOne(doc.append("md5", Mongo.md5()));
    }

    public final void updateItem(FineReportItem item){
        MongoCollection<Document> collection = scoreDatabase.getCollection("fineReport_items");
        Document query = MongoUtils.doc("itemId", item.getItemId());
        Document set = MongoUtils.doc("itemName", item.getItemName())
                .append("itemType", item.getItemType())
                .append("itemUrl", item.getItemUrl())
                .append("position", item.getPosition());
        collection.updateMany(query, MongoUtils.$set(set));
    }

    public final void deleteItem(String itemId){
        MongoCollection<Document> collection = scoreDatabase.getCollection("fineReport_items");
        Document query = MongoUtils.doc("itemId", itemId);
        collection.deleteMany(query);
    }

    public Document getItem(String itemId){
        MongoCollection<Document> collection = scoreDatabase.getCollection("fineReport_items");
        Document doc = MongoUtils.doc("itemId", itemId);
        Document first = collection.find(doc).projection(MongoUtils.doc("_id", 0).append("md5", 0)).first();
        return first;
    }

    public List<Document> getAllItems(){
        MongoCollection<Document> collection = scoreDatabase.getCollection("fineReport_items");
        return MongoUtils.toList(collection.find().projection(MongoUtils.doc("_id", 0).append("md5", 0)));
    }

    public Document item2Doc(FineReportItem item){
        Document doc = new Document();
        doc.append("itemId", item.getItemId());
        doc.append("itemName", item.getItemName());
        doc.append("itemType", item.getItemType());
        doc.append("itemUrl", item.getItemUrl());
        doc.append("position", item.getPosition());
        return doc;
    }

}
