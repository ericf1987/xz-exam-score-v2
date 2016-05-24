package com.xz.test;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengye on 2016/5/18.
 */
public class Formula {

    public void test1(){
        try{
            //链接到mongodb服务
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            //链接到test数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
            //链接到ecard集合
            //mongoDatabase.createCollection("ecard");
            MongoCollection<Document> collection = mongoDatabase.getCollection("ecard");
            //只是插入了一个行
            Document document = new Document("title", "MongoDB").append("content","start");
            for(int i=0;i<=10;i++){
                String seq = String.valueOf(i);
                document.append("title" + seq, "MongoDB" + seq).append("content" + seq, "start" + seq);
            }
            List<Document> documents = new ArrayList<Document>();
            documents.add(document);
            collection.insertMany(documents);
            System.out.println("文档插入成功！");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void queryCollections(){
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = mongoDatabase.getCollection("ecard");
            //执行更新
            collection.updateMany(Filters.eq("title0", "oracle"),
                    new Document("$set", new Document("title0", "MongoDB0")));
            //获取指定集合的所有数据的迭代器
            FindIterable<Document> findIterable = collection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while(mongoCursor.hasNext()){
                System.out.println(mongoCursor.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test1(String... s){
        String[] arr = s;
        System.out.println(s.length);
        for(String ss : arr){
            System.out.println(ss);
        }
    }

    public static void main(String[] args) {
        //new Formula().test1();
        new Formula().queryCollections();
        //new Formula().test1("123","456","aaa","ddd");
    }
}
