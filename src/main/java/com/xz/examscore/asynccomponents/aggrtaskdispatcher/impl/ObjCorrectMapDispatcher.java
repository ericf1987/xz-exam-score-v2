package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.QuestService;
import com.xz.examscore.services.RangeService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客观题正确率统计
 */
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
