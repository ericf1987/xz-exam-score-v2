package com.xz.examscore.services;

import com.xz.examscore.asynccomponents.report.ReportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
@Service
public class ReportService {

    @Autowired
    ReportManager reportManager;

    public void generateReports(String projectId, boolean async) {
        reportManager.generateReports(projectId, async);
    }
}
