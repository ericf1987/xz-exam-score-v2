package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 统计单个学生题型得分
 */
@Component
@TaskDispatcherInfo(taskType = "quest_type_score", isAdvanced = true)
public class QuestTypeScoreDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(QuestTypeScoreDispatcher.class);

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        String[] rangeKeys = new String[]{
                Range.STUDENT
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);

        int counter = 0;
        for (Range range : ranges) {
            dispatchTask(createTask(projectId, aggregationId).setRange(range));
            counter++;
            if (counter % 1000 == 0) {
                LOG.info("为项目 " + projectId + " 的 quest_type_score 统计发布了 " + counter + " 个任务");
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 quest_type_score 统计发布了 " + counter + " 个任务");
    }
}
