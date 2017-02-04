package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl.totalscore;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.TargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@TaskDispatcherInfo(taskType = "total_score_student")
public class TotalScoreStudentDispatcher extends TaskDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(TotalScoreStudentDispatcher.class);

    @Autowired
    private TargetService targetService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {

        // 知识点和能力层级不在这里统计
        List<Target> targets = targetService.queryTargets(projectId,
                Target.SUBJECT, Target.SUBJECT_COMBINATION, Target.SUBJECT_OBJECTIVE, Target.PROJECT);

        // 按班级分派任务，每个 Task 统计一个班级的每个学生的总分
        List<Range> ranges = rangesMap.get(Range.CLASS);

        int counter = 0;
        for (Target target : targets) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId).setTarget(target).setRange(range));
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 " + getTaskType() + " 统计发布了 " + counter + " 个任务");
    }
}
