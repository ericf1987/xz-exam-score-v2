package com.xz.examscore.asynccomponents.report.biz.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.asynccomponents.report.biz.classes.ClassPointAbilityLevelBiz;
import com.xz.examscore.bean.PointLevel;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xz.examscore.api.server.project.ProjectPointAbilityLevelAnalysis.filterLevels;

/**
 * @author by fengye on 2017/2/13.
 */
@Service
public class SchoolPointAbilityLevelBiz extends ClassPointAbilityLevelBiz implements Server {

    @Autowired
    TargetService targetService;

    @Autowired
    QuestService questService;

    @Autowired
    ProjectService projectService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    PointService pointService;

    @Autowired
    AverageService averageService;

    @Override
    public Result execute(Param param) throws Exception {

        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        //当前班级的所有知识点能力层级得分
        ArrayList<Document> pointLevelScores = averageService.getAverageByRangeAndTargetName(projectId, Range.school(schoolId), Target.POINT_LEVEL);

        ArrayList<Document> pointScores = averageService.getAverageByRangeAndTargetName(projectId, Range.school(schoolId), Target.POINT);

        //筛选出当前科目的知识点能力层级
        List<Document> pointLevelSubject = pointLevelScores.stream().filter(p -> {
            Document targetDoc = (Document) p.get("target");
            Document pointLevelId = (Document) targetDoc.get("id");
            PointLevel pointLevel = new PointLevel(pointLevelId.getString("point"), pointLevelId.getString("level"));
            return subjectId.equals(targetService.getTargetSubjectId(projectId, Target.pointLevel(pointLevel)));
        }).collect(Collectors.toList());

        //获取当前科目的试题
        List<Document> quests = questService.getQuests(projectId, subjectId);

        String studyStage = projectService.findProjectStudyStage(projectId);
        Map<String, Document> levelMap = abilityLevelService.queryAbilityLevels(studyStage, subjectId);

        levelMap = filterLevels(projectId, subjectId, levelMap, fullScoreService);

        List<Map<String, Object>> pointStats = packPointStats(projectId, subjectId, pointLevelSubject, pointScores, quests, levelMap);
        Map<String, Object> abilityLevelStat = packAbilityLevelStats(subjectId, levelMap);

        return Result.success()
                .set("points", pointStats)
                .set("levels", abilityLevelStat);
    }
}
