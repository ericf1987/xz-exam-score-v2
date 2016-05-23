package com.xz.services;

import com.xz.bean.ProjectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;

@Service
public class ScoreLevelService {

    @Autowired
    ProjectConfigService projectConfigService;

    public String getScoreLevel(String projectId, double scoreRate) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        Map<String, Double> scoreLevels = projectConfig.getScoreLevels();

        if (scoreRate >= scoreLevels.get(Excellent.name())) {
            return Excellent.name();
        } else if (scoreRate >= scoreLevels.get(Good.name())) {
            return Good.name();
        } else if (scoreRate >= scoreLevels.get(Pass.name())) {
            return Pass.name();
        } else {
            return Fail.name();
        }
    }
}
