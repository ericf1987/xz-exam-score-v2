package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.services.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 有些项目需要对文科理科分数合起来统计
 */
@TaskDispatcherInfo(taskType = "combined_total_score", dependentTaskType = "total_score")
@Component
public class CombinedSubjectScoreDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(CombinedSubjectScoreDispatcher.class);

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        if (!projectConfig.isCombineCategorySubjects()) {
            LOG.info("项目" + projectId + "不存在文理科合并，无需统计");
            return;
        }

        dispatchTaskForEveryStudent(projectId, aggregationId);
    }
}
