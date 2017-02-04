package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

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
 * 有些项目需要对文科理科分数合起来统计
 */
@TaskDispatcherInfo(taskType = "combined_total_score", dependentTaskType = "total_score_province")
@Component
public class CombinedSubjectScoreDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(CombinedSubjectScoreDispatcher.class);

    @Autowired
    StudentService studentService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {

        String[] rangeKeys = new String[]{
                Range.STUDENT
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);

        if (!projectConfig.isCombineCategorySubjects()) {
            LOG.info("项目" + projectId + "不存在文理科合并，无需统计");
            return;
        }

        int counter = 0;
        for (Range range : ranges) {
            dispatchTask(createTask(projectId, aggregationId).setRange(range));
            counter++;
        }
        LOG.info("最终为项目 " + projectId + " 的 combined_subject 统计发布了 " + counter + " 个任务");
    }
}
