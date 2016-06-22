package com.xz.services;

import com.xz.ajiaedu.common.redis.Redis;
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

    @Autowired
    Redis redis;

    public ProjectStatus getProjectStatus(String projectId) {
        Document project = projectService.findProject(projectId);

        if (project == null) {
            return ProjectStatus.Empty;
        } else {
            String statusName = redis.get(getStatusKey(projectId));
            return statusName == null ? ProjectStatus.ProjectImported : ProjectStatus.valueOf(statusName);
        }
    }

    public void setProjectStatus(String projectId, ProjectStatus status) {
        redis.set(getStatusKey(projectId), status.name());
    }

    private String getStatusKey(String projectId) {
        return "project:status:" + projectId;
    }
}
