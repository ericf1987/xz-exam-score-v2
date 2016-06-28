package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.services.StudentService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注意：报表中的得分率都是算出来的，这里的得分率统计只为四率人数统计服务
 * <p>
 * 得分率和得分等级统计。此处只统计考生个人的科目/项目得分率和得分等级，不会
 * 统计班级/学校的得分率和个人的知识点/能力层级得分率，因为前者还需要进一步
 * 统计四率人数，后者只需要取平均分除以满分值即可。
 */
@TaskDispatcherInfo(taskType = "score_rate", dependentTaskType = "total_score")
@Component
public class ScoreRateDispatcher extends TaskDispatcher {

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        dispatchTaskForEveryStudent(projectId, aggregationId, studentService);
    }
}
