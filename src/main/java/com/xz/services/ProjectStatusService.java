package com.xz.services;

import com.xz.bean.ProjectStatus;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (description)
 * created at 16/06/22
 *
 * @author yiding_he
 */
@Service
public class ProjectStatusService {

    @Autowired
    ProjectService projectService;

    public ProjectStatus getProjectStatus(String projectId) {
        Document project = projectService.findProject(projectId);

        if (project == null) {
            return ProjectStatus.Empty;
        } else {
            return ProjectStatus.Imported;
        }
    }
}
