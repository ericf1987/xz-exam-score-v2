package com.xz.examscore.asynccomponents.importproject;

import com.xz.examscore.asynccomponents.QueueMessage;

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

    public ImportTaskMessage() {
    }

    public ImportTaskMessage(String projectId, boolean importProjectInfo, boolean importProjectScore, boolean inAggrProcess) {
        this.projectId = projectId;
        this.importProjectInfo = importProjectInfo;
        this.importProjectScore = importProjectScore;
        this.inAggrProcess = inAggrProcess;
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
