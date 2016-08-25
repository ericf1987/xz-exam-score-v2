package com.xz.examscore.services;

import com.xz.ajiaedu.common.redis.Redis;
import com.xz.examscore.bean.ProjectStatus;
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

    public void setProjectStatus(String projectId, ProjectStatus status) {
        projectService.setProjectStatus(projectId, status);
    }

    public ProjectStatus getProjectStatus(String projectId) {
        return projectService.getProjectStatus(projectId);
    }
}
