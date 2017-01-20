package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.TargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@TaskDispatcherInfo(taskType = "total_score")
@Component
public class TotalScoreTaskDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(TotalScoreTaskDispatcher.class);

    @Autowired
    TargetService targetService;

    @Autowired
    ScoreService scoreService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        // 知识点和能力层级不在这里统计
        List<Target> targets = targetService.queryTargets(projectId,
                Target.QUEST, Target.SUBJECT, Target.SUBJECT_COMBINATION, Target.SUBJECT_OBJECTIVE, Target.PROJECT);
        int counter = 0;
        for (Target target : targets) {
            dispatchTask(createTask(projectId, aggregationId).setTarget(target));
            counter++;
        }
        LOG.info("最终为项目 " + projectId + " 的 total_score 统计发布了 " + counter + " 个任务");
    }
}
