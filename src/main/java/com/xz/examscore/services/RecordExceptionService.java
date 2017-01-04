package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.bean.ProjectStatus;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2016/12/16.
 */
@Service
public class RecordExceptionService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 记录异常信息
     *
     * @param projectId     项目名称
     * @param projectStatus 统计执行阶段
     * @param e             异常
     */
    public void recordException(String projectId, ProjectStatus projectStatus, Exception e, String desc) {
        String exceptionName = e.getClass().getName();
        String exceptionDesc = StringUtil.isBlank(e.getMessage()) ? desc : e.getMessage();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(Calendar.getInstance().getTime());
        MongoCollection<Document> collection = scoreDatabase.getCollection("exception_list");
        collection.insertOne(doc("project", projectId).
                append("name", exceptionName).
                append("desc", exceptionDesc).
                append("projectStatus", projectStatus.name()).
                append("time", time));
    }

    /**
     * 删除异常记录
     *
     * @param projectId     项目名称
     * @param projectStatus 统计执行阶段
     */
    public void deleteExceptionRecord(String projectId, ProjectStatus projectStatus) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("exception_list");
        Document query = doc("project", projectId);
        if (null != projectStatus) {
            query.append("projectStatus", projectStatus.name());
        }
        collection.deleteMany(query);
    }

    /**
     * 查询异常记录
     *
     * @param projectId     项目名称
     * @param projectStatus 统计执行阶段
     */
    public Document findExceptionRecord(String projectId, ProjectStatus projectStatus){
        MongoCollection<Document> collection = scoreDatabase.getCollection("exception_list");
        Document query = doc("project", projectId);
        if(null != projectStatus){
            query.append("projectStatus", projectStatus.name());
        }
        return collection.find(query).first();
    }
}
