package com.xz.examscore.asynccomponents.report;

import com.xz.examscore.asynccomponents.MessageReceiver;
import com.xz.examscore.services.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    protected void executeTask(ReportTaskMessage message) {
        reportService.generateReports(message.getProjectId(), true);
    }
}
