package com.xz.taskdispatchers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Target;
import com.xz.services.StudentService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@TaskDispatcherInfo(taskType = "point", isAdvanced = true)
@Component
public class PointDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(PointDispatcher.class);

    @Autowired
    StudentService studentService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        // 删除旧数据
        LOG.info("删除项目 {} 的 point 相关旧数据...", projectId);
        MongoCollection<Document> c = scoreDatabase.getCollection("total_score");
        c.deleteMany(doc("project", projectId).append("target.name", Target.POINT));
        c.deleteMany(doc("project", projectId).append("target.name", Target.POINT_LEVEL));
        c.deleteMany(doc("project", projectId).append("target.name", Target.SUBJECT_LEVEL));

        LOG.info("项目 {} 的 point 相关旧数据删除完毕，开始统计...", projectId);
        dispatchTaskForEveryStudent(projectId, aggregationId, studentService);
    }

}
