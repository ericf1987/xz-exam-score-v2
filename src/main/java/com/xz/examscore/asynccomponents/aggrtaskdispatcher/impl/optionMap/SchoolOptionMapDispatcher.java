package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl.optionMap;

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
 * @author by fengye on 2017/1/30.
 */
@Component
@TaskDispatcherInfo(taskType = "school_option_map", dependentTaskType = "class_option_map")
public class SchoolOptionMapDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(SchoolOptionMapDispatcher.class);

    @Autowired
    QuestService questService;

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        String[] rangeKey = new String[]{
                Range.SCHOOL
        };

        List<Range> ranges = fetchRanges(rangeKey, rangesMap);

        List<Document> objectiveQuests = questService.getQuests(projectId, true);

        int counter = 0;

        for (Document quest : objectiveQuests) {
            for (Range range : ranges) {
                dispatchTask(
                        createTask(projectId, aggregationId)
                                .setRange(range)
                                .setTarget(Target.quest(quest.getString("questId")))
                );
                counter++;
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 school_option_map 统计发布了 " + counter + " 个任务");
    }
}
