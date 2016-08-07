package com.xz.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2016/8/7.
 */
public class AggregationProgressParam {
    public static final Map<String, String> PROGRESS_MAP = new HashMap<>();

    static{
        PROGRESS_MAP.put("Empty", "0");
        PROGRESS_MAP.put("ProjectImporting", "0.1");
        PROGRESS_MAP.put("ProjectImported", "0.2");
        PROGRESS_MAP.put("ScoreImporting", "0.3");
        PROGRESS_MAP.put("ScoreImported", "0.4");
        PROGRESS_MAP.put("AggregationStarted", "0.5");
        PROGRESS_MAP.put("AggregationCompleted", "1");
        PROGRESS_MAP.put("ReportGenerating", "0.9");
        PROGRESS_MAP.put("ReportGenerated", "1");
    }
}
