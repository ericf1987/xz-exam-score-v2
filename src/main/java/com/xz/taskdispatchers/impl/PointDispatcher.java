package com.xz.taskdispatchers.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Target;
import com.xz.services.StudentService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$in;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@TaskDispatcherInfo(taskType = "point")
@Component
public class PointDispatcher extends TaskDispatcher {

    @Autowired
    StudentService studentService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        // 删除旧数据
        scoreDatabase.getCollection("total_score").deleteMany(
                doc("project", projectId).append("target.name", $in(Target.POINT, Target.POINT_LEVEL)));

        dispatchTaskForEveryStudent(projectId, aggregationId, studentService);
    }

}
