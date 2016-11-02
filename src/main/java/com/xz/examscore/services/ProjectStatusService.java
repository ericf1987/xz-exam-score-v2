package com.xz.examscore.services;

import com.xz.ajiaedu.common.redis.Redis;
import com.xz.examscore.bean.AggregationStatus;
import com.xz.examscore.bean.ProjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 管理项目状态，具体参考 {@link ProjectStatus} 类
 *
 * @author yiding_he
 */
@Service
public class ProjectStatusService {

    static final Logger LOG = LoggerFactory.getLogger(ProjectStatusService.class);

    @Autowired
    ProjectService projectService;

    @Autowired
    Redis redis;

    public void setProjectStatus(String projectId, ProjectStatus status) {
        try {
            projectService.setProjectStatus(projectId, status);
        } catch (Exception e) {
            LOG.info("修改项目 " + projectId + " 状态为 " + status + " 失败", e);
        }
    }

    public ProjectStatus getProjectStatus(String projectId) {
        return projectService.getProjectStatus(projectId);
    }

    public AggregationStatus getAggregationStatus(String projectId) {
        return projectService.getAggregationStatus(projectId);
    }

    public void setAggregationStatus(String projectId, AggregationStatus activated) {
        try {
            projectService.setAggregationStatus(projectId, activated);
        } catch (Exception e) {
            LOG.info("修改项目 " + projectId + " 统计状态为 " + activated + " 失败", e);
        }
    }
}
