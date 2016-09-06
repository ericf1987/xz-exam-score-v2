package com.xz.examscore.asynccomponents.importproject;

import com.xz.examscore.asynccomponents.QueueMessage;
import com.xz.examscore.bean.AggregationType;

/**
 * 对消息队列记录的包装：导入项目任务
 *
 * @author yiding_he
 */
public class ImportTaskMessage implements QueueMessage {

    /**
     * 要导入的项目ID
     */
    private String projectId;

    /**
     * 是否导入项目基本信息
     */
    private boolean importProjectInfo;

    /**
     * 是否导入网阅成绩信息（仅限网阅）
     */
    private boolean importProjectScore;

    /**
     * 是否在统计过程当中（若为 true，则完成导入完成后要发送一条 DispatchTaskMessage）
     */
    private boolean inAggrProcess;

    /**
     * 统计方式，仅当 inAggrProcess 为 true 时有效
     */
    private AggregationType aggregationType;

    /**
     * 是否要生成报表，仅当 inAggrProcess 为 true 时有效
     */
    private boolean generateReport;

    /**
     * 是否要上传成绩到阿里云，仅当 inAggrProcess 为 true 时有效
     */
    private boolean exportScore;

    public boolean isExportScore() {
        return exportScore;
    }

    public void setExportScore(boolean exportScore) {
        this.exportScore = exportScore;
    }

    public ImportTaskMessage() {
    }

    public ImportTaskMessage(String projectId, boolean importProjectInfo, boolean importProjectScore) {
        this(projectId, importProjectInfo, importProjectScore, false);
    }

    public ImportTaskMessage(String projectId, boolean importProjectInfo, boolean importProjectScore, boolean inAggrProcess) {
        this.projectId = projectId;
        this.importProjectInfo = importProjectInfo;
        this.importProjectScore = importProjectScore;
        this.inAggrProcess = inAggrProcess;
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

    public boolean isInAggrProcess() {
        return inAggrProcess;
    }

    public void setInAggrProcess(boolean inAggrProcess) {
        this.inAggrProcess = inAggrProcess;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean isImportProjectInfo() {
        return importProjectInfo;
    }

    public void setImportProjectInfo(boolean importProjectInfo) {
        this.importProjectInfo = importProjectInfo;
    }

    public boolean isImportProjectScore() {
        return importProjectScore;
    }

    public void setImportProjectScore(boolean importProjectScore) {
        this.importProjectScore = importProjectScore;
    }
}
