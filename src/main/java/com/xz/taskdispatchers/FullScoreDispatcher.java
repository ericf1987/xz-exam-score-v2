package com.xz.taskdispatchers;

import com.xz.bean.ProjectConfig;
import com.xz.bean.Target;
import com.xz.services.TargetService;
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
                Target.QUEST_TYPE, Target.SUBJECT_OBJECTIVE, Target.POINT, Target.ABILITY_LEVEL);

        for (Target target : targets) {
            dispatchTask(createTask(projectId, aggregationId).setTarget(target));
        }
    }
}
