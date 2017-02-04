package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl.totalscore;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import org.springframework.stereotype.Component;

@Component
@TaskDispatcherInfo(taskType = "total_score_school", dependentTaskType = "total_score_class")
public class TotalScoreSchoolDispatcher extends TotalScoreClassDispatcher {
}
