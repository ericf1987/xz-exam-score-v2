package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.project.ProjectPointAbilityLevelAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectPointAbilityLevelAnalysis.*;

/**
 * 学校成绩-知识点能力层级(双向细目)分析
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-知识点能力层级(双向细目)分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级id,默认学校的第一个班级", required = false)
})
@Service
public class SchoolPointAbilityLevelAnalysis implements Server {

    @Autowired
    PointService pointService;

    @Autowired
    QuestService questService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    ClassService classService;

    @Autowired
    ProjectService projectService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    ProjectPointAbilityLevelAnalysis projectPointAbilityLevelAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");
        String classId = param.getString("classId");

        if (StringUtil.isBlank(classId)) {
            List<Document> listClasses = new ArrayList<>(classService.listClasses(projectId, schoolId));
            if (!listClasses.isEmpty()) {
                listClasses.sort((o1, o2) -> o1.getString("name").compareTo(o2.getString("name")));
                classId = listClasses.get(0).getString("class");
            }
        }

        if (StringUtil.isBlank(classId)) {
            return Result.fail("找不到学校的班级");
        }

        Range range = Range.clazz(classId);
        String studyStage = projectService.findProjectStudyStage(projectId);
        Map<String, Document> levelMap = abilityLevelService.queryAbilityLevels(studyStage, subjectId);
        levelMap = filterLevels(projectId, subjectId, levelMap, fullScoreService);

        List<Map<String, Object>> pointStats = projectPointAbilityLevelAnalysis.getPointAnalysis(projectId, subjectId, range, levelMap);
        Map<String, Object> abilityLevelStat = projectPointAbilityLevelAnalysis.getAbilityLevelStats(projectId, subjectId, range,
                levelMap);

        return Result.success()
                .set("points", pointStats)
                .set("levels", abilityLevelStat)
                .set("hasHeader", !((List) abilityLevelStat.get("levelInfos")).isEmpty());
    }
}
