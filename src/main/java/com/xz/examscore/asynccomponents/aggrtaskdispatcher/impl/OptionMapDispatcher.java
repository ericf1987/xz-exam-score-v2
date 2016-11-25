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
 * 客观题各选项人数、比率
 *
 * @author yiding_he
 */
@Component
@TaskDispatcherInfo(taskType = "option_map")
public class OptionMapDispatcher extends TaskDispatcher {

    static final Logger LOG = LoggerFactory.getLogger(OptionMapDispatcher.class);

    @Autowired
    QuestService questService;

    @Autowired
    RangeService rangeService;

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig, Map<String, List<Range>> rangesMap) {
        List<Document> objecitveQuests = questService.getQuests(projectId, true);
        String[] rangeKeys = new String[]{
                Range.CLASS, Range.SCHOOL, Range.PROVINCE
        };

        List<Range> ranges = fetchRanges(rangeKeys, rangesMap);

        // 对每个学校、班级和每个客观题发布一个任务
        int counter = 0;
        for (Document quest : objecitveQuests) {
            for (Range range : ranges) {
                dispatchTask(createTask(projectId, aggregationId)
                        .setRange(range)
                        .setTarget(Target.quest(quest.getString("questId"))));
                counter++;
                if (counter % 1000 == 0) {
                    LOG.info("为项目 " + projectId + " 的 option_map 统计发布了 " + counter + " 个任务");
                }
            }
        }
        LOG.info("最终为项目 " + projectId + " 的 option_map 统计发布了 " + counter + " 个任务");
    }
}
