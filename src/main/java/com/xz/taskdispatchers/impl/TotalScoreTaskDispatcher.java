package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.bean.Target;
import com.xz.services.TargetService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TaskDispatcherInfo(taskType = "total_score")
@Component
public class TotalScoreTaskDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(TotalScoreTaskDispatcher.class);

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        // 知识点和能力层级不在这里统计
        List<Target> targets = targetService.queryTargets(projectId,
                Target.QUEST, Target.SUBJECT, Target.PROJECT, Target.SUBJECT_OBJECTIVE);

        int counter = 0;
            for (Target target : targets) {
                dispatchTask(createTask(projectId, aggregationId).setTarget(target));
                counter++;
                if (counter % 1000 == 0) {
                    LOG.info("为项目 " + projectId + " 的 total_score 统计发布了 " + counter + " 个任务");
                }
        }
        LOG.info("最终为项目 " + projectId + " 的 total_score 统计发布了 " + counter + " 个任务");
    }
}
