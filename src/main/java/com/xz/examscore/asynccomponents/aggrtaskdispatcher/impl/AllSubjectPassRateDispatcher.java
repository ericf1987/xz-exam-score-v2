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
 * 全科及格率/全科不及格率
 *
 * @author yiding_he
 */
@Component
@TaskDispatcherInfo(taskType = "all_subject_pass_rate", dependentTaskType = "score_rate")
public class AllSubjectPassRateDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(AllSubjectPassRateDispatcher.class);

    @Autowired
    RangeService rangeService;


    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {

        String[] rangeKeys = new String[]{
                Range.CLASS, Range.SCHOOL, Range.PROVINCE
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);

        int counter = 0;
        for (Range range : ranges) {
            dispatchTask(createTask(projectId, aggregationId).setRange(range));
            counter++;
        }
        LOG.info("最终为项目 " + projectId + " 的 all_subject_pass_rate 统计发布了 " + counter + " 个任务");
    }

}
