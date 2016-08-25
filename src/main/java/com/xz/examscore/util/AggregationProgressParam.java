package com.xz.examscore.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2016/8/7.
 */
public class AggregationProgressParam {
    public static final Map<String, String> PROGRESS_MAP = new HashMap<>();

    public static final Map<String, String> PROGRESS_MAP_STATUS = new HashMap<>();

    static{
        PROGRESS_MAP.put("Empty", "0");
        PROGRESS_MAP.put("ProjectImporting", "0.1");
        PROGRESS_MAP.put("ProjectImported", "0.2");
        PROGRESS_MAP.put("ScoreImporting", "0.3");
        PROGRESS_MAP.put("ScoreImported", "0.4");
        PROGRESS_MAP.put("AggregationStarted", "0.5");
        PROGRESS_MAP.put("AggregationFailed", "0.5");
        PROGRESS_MAP.put("AggregationCompleted", "1");
        PROGRESS_MAP.put("ReportGenerating", "0.9");
        PROGRESS_MAP.put("ReportGenerateTimeOut", "0.9");
        PROGRESS_MAP.put("ReportGenerated", "1");
    }

    static{
        PROGRESS_MAP_STATUS.put("Empty", "0");
        PROGRESS_MAP_STATUS.put("ProjectImporting", "0");
        PROGRESS_MAP_STATUS.put("ProjectImported", "1");
        PROGRESS_MAP_STATUS.put("ScoreImporting", "0");
        PROGRESS_MAP_STATUS.put("ScoreImported", "1");
        PROGRESS_MAP_STATUS.put("AggregationStarted", "0");
        PROGRESS_MAP_STATUS.put("AggregationFailed", "0");
        PROGRESS_MAP_STATUS.put("AggregationCompleted", "1");
        PROGRESS_MAP_STATUS.put("ReportGenerating", "0");
        PROGRESS_MAP_STATUS.put("ReportGenerateTimeOut", "0");
        PROGRESS_MAP_STATUS.put("ReportGenerated", "1");
    }


}
