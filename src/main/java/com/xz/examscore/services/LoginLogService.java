package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 登录日志处理
 * created at 2016/10/13.
 *
 * @author zhaorenwu
 */
@Service
public class LoginLogService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 添加用户登录日志
     *
     * @param userId        用户id
     * @param userName      用户名称
     * @param role          用户角色
     * @param mobile        手机号码
     * @param schoolId      学校id
     * @param schoolName    学校名称
     */
    public void addLoginLog(String userId, String userName, String role,
                            String mobile, String schoolId, String schoolName) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("login_log");

        Document doc = new Document();
        doc.put("user_id", userId);
        doc.put("user_name", userName);
        doc.put("role", role);
        doc.put("mobile", mobile);
        doc.put("school_id", schoolId);
        doc.put("school_name", schoolName);
        doc.put("ctime", new Date());

        collection.insertOne(doc);
    }
}
