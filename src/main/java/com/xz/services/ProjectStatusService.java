package com.xz.services;

import com.xz.ajiaedu.common.redis.Redis;
import com.xz.bean.ProjectStatus;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 管理项目状态，具体参考 {@link ProjectStatus} 类
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
