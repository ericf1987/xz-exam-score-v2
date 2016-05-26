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

@Component
@TaskDispatcherInfo(taskType = "obj_correct_map")
public class ObjCorrectMapDispatcher extends TaskDispatcher {

    @Autowired
    RangeService rangeService;

    @Autowired
    QuestService questService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {
        List<Range> ranges = rangeService.queryRanges(projectId, Range.PROVINCE, Range.SCHOOL, Range.CLASS);
        List<Document> quests = questService.getQuests(projectId, true);

        for (Range range : ranges) {
            for (Document quest : quests) {
                String questId = quest.getString("questId");
                dispatchTask(createTask(projectId, aggregationId)
                        .setTarget(Target.quest(questId)).setRange(range));
            }
        }
    }
}
