package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.RangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 非学生的题型得分（平均分）统计
 */
@TaskDispatcherInfo(taskType = "quest_type_score_average", dependentTaskType = "quest_type_score", isAdvanced = true)
@Component
public class QuestTypeScoreAverageDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(QuestTypeScoreAverageDispatcher.class);

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {

        String[] rangeKeys = new String[]{
                Range.PROVINCE, Range.CITY, Range.AREA, Range.SCHOOL, Range.CLASS
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);

        int counter = 0;
        for (Range range : ranges) {
            dispatchTask(createTask(projectId, aggregationId).setRange(range));
            counter++;
            if (counter % 1000 == 0) {
                LOG.info("为项目 " + projectId + " 的 quest_type_score_average 统计发布了 " + counter + " 个任务");
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 quest_type_score_average 统计发布了 " + counter + " 个任务");
    }
}
