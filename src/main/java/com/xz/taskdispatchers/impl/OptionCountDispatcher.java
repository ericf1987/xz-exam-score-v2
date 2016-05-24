package com.xz.taskdispatchers.impl;

import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.QuestService;
import com.xz.services.RangeService;
import com.xz.taskdispatchers.TaskDispatcher;
import com.xz.taskdispatchers.TaskDispatcherInfo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客观题各选项人数、比率
 *
 * @author yiding_he
 */
@Component
@TaskDispatcherInfo(taskType = "option_count", dependentTaskType = "student_list")
public class OptionCountDispatcher extends TaskDispatcher {

    @Autowired
    QuestService questService;

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        List<Document> quests = questService.getQuests(projectId, true);
        List<Range> ranges = rangeService.queryRanges(projectId, Range.SCHOOL);

        // 对每个学校和每个客观题发布一个任务
        for (Document quest : quests) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId)
                        .setRange(range)
                        .setTarget(Target.quest(quest.getString("questId"))));
            }
        }
    }
}
