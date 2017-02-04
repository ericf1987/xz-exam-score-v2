package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl.totalscore;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import org.springframework.stereotype.Component;

@Component
@TaskDispatcherInfo(taskType = "total_score_province", dependentTaskType = "total_score_school")
public class TotalScoreProvinceDispatcher extends TotalScoreClassDispatcher {
}
