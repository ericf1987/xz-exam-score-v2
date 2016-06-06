package com.xz.taskdispatchers.impl;

import com.mongodb.client.FindIterable;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.services.RankService;
import com.xz.services.StudentService;
import com.xz.services.TargetService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TaskDispatcherInfo(taskType = "score_map", dependentTaskType = "combined_total_score")
@Component
public class ScoreMapDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(ScoreMapDispatcher.class);

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankService rankService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        LOG.info("删除项目{}的排名统计结果...", projectId);
        rankService.deleteRanks(projectId);

        LOG.info("开始对项目{}统计排名...", projectId);
        FindIterable<Document> studentList = studentService.getProjectStudentList(projectId, null);

        for (Document student : studentList) {
            String studentId = student.getString("student");
            dispatchTask(createTask(projectId, aggregationId).setRange(Range.student(studentId)));
        }
    }
}
