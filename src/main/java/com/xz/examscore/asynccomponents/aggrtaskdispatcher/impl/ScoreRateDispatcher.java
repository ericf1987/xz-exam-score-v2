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
 * 注意：报表中的得分率都是算出来的，这里的得分率统计只为四率人数统计服务
 * <p>
 * 得分率和得分等级统计。此处只统计考生个人的科目/项目得分率和得分等级，不会
 * 统计班级/学校的得分率和个人的知识点/能力层级得分率，因为前者还需要进一步
 * 统计四率人数，后者只需要取平均分除以满分值即可。
 */
@TaskDispatcherInfo(taskType = "score_rate", dependentTaskType = "total_score")
@Component
public class ScoreRateDispatcher extends TaskDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(ScoreRateDispatcher.class);

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
                LOG.info("为项目 " + projectId + " 的 score_rate 统计发布了 " + counter + " 个任务");
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 score_rate 统计发布了 " + counter + " 个任务");
    }
}
