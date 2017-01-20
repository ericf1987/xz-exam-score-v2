package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl.totalscore;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@TaskDispatcherInfo(taskType = "total_score_school", dependentTaskType = "total_score_class")
public class TotalScoreSchoolDispatcher extends TaskDispatcher {

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {

    }
}
