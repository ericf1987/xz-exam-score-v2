package com.xz.examscore.asynccomponents.report;

import com.xz.examscore.asynccomponents.QueueMessage;

/**
 * 对消息队列记录的包装
 *
 * @author yiding_he
 */
public class ReportTaskMessage implements QueueMessage {

    private String projectId;

    public ReportTaskMessage() {
    }

    public ReportTaskMessage(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
