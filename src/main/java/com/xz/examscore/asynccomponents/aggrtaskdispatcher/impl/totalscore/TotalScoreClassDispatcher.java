package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl.totalscore;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@TaskDispatcherInfo(taskType = "total_score_class", dependentTaskType = "total_score_student")
public class TotalScoreClassDispatcher extends TaskDispatcher {

    @Autowired
    private TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {

        // 知识点和能力层级不在这里统计
        List<Target> targets = targetService.queryTargets(projectId,
                Target.QUEST, Target.SUBJECT, Target.SUBJECT_COMBINATION, Target.SUBJECT_OBJECTIVE, Target.PROJECT);

        for (Target target : targets) {
            dispatchTask(createTask(projectId, aggregationId).setTarget(target));
        }
    }
}
