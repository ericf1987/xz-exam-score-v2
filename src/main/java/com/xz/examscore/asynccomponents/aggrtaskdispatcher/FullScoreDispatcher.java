package com.xz.examscore.asynccomponents.aggrtaskdispatcher;

import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 补完一些满分，例如主观题客观题满分，题型知识点满分等等
 */
@Component
@TaskDispatcherInfo(taskType = "full_score")
public class FullScoreDispatcher extends TaskDispatcher {

    @Autowired
    TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        List<Target> targets = targetService.queryTargets(projectId,
                Target.QUEST_TYPE, Target.SUBJECT_OBJECTIVE, Target.POINT);
        int counter = 0;
        for (Target target : targets) {
            dispatchTask(createTask(projectId, aggregationId).setTarget(target));
            counter++;
            if (counter % 1000 == 0) {
                LOG.info("为项目 " + projectId + " 的 full_score 统计发布了 " + counter + " 个任务");
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 full_score 统计发布了 " + counter + " 个任务");
    }
}
