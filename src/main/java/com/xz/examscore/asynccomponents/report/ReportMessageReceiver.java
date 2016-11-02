package com.xz.examscore.asynccomponents.report;

import com.xz.examscore.asynccomponents.MessageReceiver;
import com.xz.examscore.bean.AggregationStatus;
import com.xz.examscore.services.ProjectStatusService;
import com.xz.examscore.services.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xz.examscore.bean.ProjectStatus.ReportGenerated;
import static com.xz.examscore.bean.ProjectStatus.ReportGenerating;

/**
 * (description)
 * created at 16/08/26
 *
 * @author yiding_he
 */
@Component
public class ReportMessageReceiver extends MessageReceiver<ReportTaskMessage> {

    static final Logger LOG = LoggerFactory.getLogger(ReportMessageReceiver.class);

    @Autowired
    ReportService reportService;

    @Autowired
    ProjectStatusService projectStatusService;

    @Override
    protected void executeTask(ReportTaskMessage message) {
        projectStatusService.setAggregationStatus(message.getProjectId(), AggregationStatus.Activated);
        projectStatusService.setProjectStatus(message.getProjectId(), ReportGenerating);
        reportService.generateReports(message.getProjectId(), true);
        projectStatusService.setProjectStatus(message.getProjectId(), ReportGenerated);
        projectStatusService.setAggregationStatus(message.getProjectId(), AggregationStatus.Terminated);
    }
}
