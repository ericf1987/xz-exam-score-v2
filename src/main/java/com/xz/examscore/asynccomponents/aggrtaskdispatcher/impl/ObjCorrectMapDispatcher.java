package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.QuestService;
import com.xz.examscore.services.RangeService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 客观题正确率统计
 */
@Component
@TaskDispatcherInfo(taskType = "obj_correct_map")
public class ObjCorrectMapDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(ObjCorrectMapDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Autowired
    QuestService questService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        String[] rangeKeys = new String[]{
                Range.CLASS, Range.SCHOOL, Range.PROVINCE
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);

        List<Document> quests = questService.getQuests(projectId, true);

        int counter = 0;
        for (Range range : ranges) {
            for (Document quest : quests) {
                String questId = quest.getString("questId");
                dispatchTask(createTask(projectId, aggregationId)
                        .setTarget(Target.quest(questId)).setRange(range));
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 obj_correct_map 统计发布了 " + counter + " 个任务");
    }
}
