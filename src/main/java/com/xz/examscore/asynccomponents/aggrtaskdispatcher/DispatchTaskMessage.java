package com.xz.examscore.asynccomponents.aggrtaskdispatcher;

import com.xz.examscore.asynccomponents.QueueMessage;
import com.xz.examscore.bean.AggregationType;

/**
 * 统计任务消息，DispatchMessageReceiver 要按照消息里的要求来执行
 *
 * @author yiding_he
 */
public class DispatchTaskMessage implements QueueMessage {

    private String projectId;           // 要统计的项目ID

    private AggregationType aggregationType;    // 统计方式

    private boolean generateReport;     // 是否要生成报表

    private boolean exportScore;        // 是否要导出成绩

    public boolean isExportScore() {
        return exportScore;
    }

    public void setExportScore(boolean exportScore) {
        this.exportScore = exportScore;
    }

    public DispatchTaskMessage() {
    }

    public DispatchTaskMessage(String projectId, AggregationType aggregationType) {
        this.projectId = projectId;
        this.aggregationType = aggregationType;
    }

    public DispatchTaskMessage(String projectId, AggregationType aggregationType, boolean generateReport, boolean exportScore) {
        this.projectId = projectId;
        this.aggregationType = aggregationType;
        this.generateReport = generateReport;
        this.exportScore = exportScore;
    }

    public boolean isGenerateReport() {
        return generateReport;
    }

    public void setGenerateReport(boolean generateReport) {
        this.generateReport = generateReport;
    }

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(AggregationType aggregationType) {
        this.aggregationType = aggregationType;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
