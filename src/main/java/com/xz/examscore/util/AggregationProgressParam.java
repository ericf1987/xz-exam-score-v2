package com.xz.examscore.util;

import com.xz.examscore.bean.ProjectStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2016/8/7.
 */
public class AggregationProgressParam {
    public static final Map<String, String> PROGRESS_MAP = new HashMap<>();

    public static final Map<String, String> PROGRESS_MAP_STATUS = new HashMap<>();

    //统计进度
    static{
        PROGRESS_MAP.put(ProjectStatus.Empty.name(), "0");
        PROGRESS_MAP.put(ProjectStatus.Initializing.name(), "0");
        PROGRESS_MAP.put(ProjectStatus.ProjectImporting.name(), "0.1");
        PROGRESS_MAP.put(ProjectStatus.ProjectImported.name(), "0.2");
        PROGRESS_MAP.put(ProjectStatus.ScoreImporting.name(), "0.3");
        PROGRESS_MAP.put(ProjectStatus.ScoreImported.name(), "0.4");
        PROGRESS_MAP.put(ProjectStatus.AggregationStarted.name(), "0.5");
        PROGRESS_MAP.put(ProjectStatus.AggregationFailed.name(), "0.5");
        PROGRESS_MAP.put(ProjectStatus.AggregationCompleted.name(), "1");
        PROGRESS_MAP.put(ProjectStatus.ReportGenerating.name(), "1");
        PROGRESS_MAP.put(ProjectStatus.ReportGenerateTimeOut.name(), "1");
        PROGRESS_MAP.put(ProjectStatus.ReportGenerated.name(), "1");
    }

    //是否执行完成
    static{
        PROGRESS_MAP_STATUS.put(ProjectStatus.Empty.name(), "0");
        PROGRESS_MAP_STATUS.put(ProjectStatus.Initializing.name(), "0");
        PROGRESS_MAP_STATUS.put(ProjectStatus.ProjectImporting.name(), "0");
        PROGRESS_MAP_STATUS.put(ProjectStatus.ProjectImported.name(), "1");
        PROGRESS_MAP_STATUS.put(ProjectStatus.ScoreImporting.name(), "0");
        PROGRESS_MAP_STATUS.put(ProjectStatus.ScoreImported.name(), "1");
        PROGRESS_MAP_STATUS.put(ProjectStatus.AggregationStarted.name(), "0");
        PROGRESS_MAP_STATUS.put(ProjectStatus.AggregationFailed.name(), "0");
        PROGRESS_MAP_STATUS.put(ProjectStatus.AggregationCompleted.name(), "1");
        PROGRESS_MAP_STATUS.put(ProjectStatus.ReportGenerating.name(), "1");
        PROGRESS_MAP_STATUS.put(ProjectStatus.ReportGenerateTimeOut.name(), "1");
        PROGRESS_MAP_STATUS.put(ProjectStatus.ReportGenerated.name(), "1");
    }


}
