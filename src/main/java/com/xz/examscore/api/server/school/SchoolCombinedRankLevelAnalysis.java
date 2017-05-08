package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassCombinedRankLevelAnalysis;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/12/20.
 */
@Function(description = "学校成绩-等第排名统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolCombinedRankLevelAnalysis implements Server {

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    ClassCombinedRankLevelAnalysis classCombinedRankLevelAnalysis;

    @Override
    public Result execute(Param param) throws Exception {

        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        String lastRankLevel = projectConfig.getLastRankLevel();
        List<Map<String, Object>> students = new ArrayList<>();

        //非组合科目
        List<String> nonCombinedSubjectIds = new ArrayList<>(subjectService.querySubjects(projectId)).stream().filter(
                subjectId -> !StringUtil.isOneOf(subjectId, "004", "005", "006", "007", "008", "009", "004005006", "007008009"))
                .collect(Collectors.toList());
        //组合科目
        List<String> combinedSubjectIds = new ArrayList<>(subjectCombinationService.getAllSubjectCombinations(projectId));

        //所有科目
        List<String> subjectIds = subjectService.querySubjects(projectId);

        if(combinedSubjectIds.isEmpty()){
            classCombinedRankLevelAnalysis.processRankLevelAnalysis(projectId, Range.school(schoolId), lastRankLevel,
                    students, subjectIds, Collections.emptyList(), Range.SCHOOL);
            return Result.success().set("subjectIds", subjectIds)
                    .set("students", students);
        }else{
            classCombinedRankLevelAnalysis.processRankLevelAnalysis(projectId, Range.school(schoolId), lastRankLevel,
                    students, nonCombinedSubjectIds, combinedSubjectIds, Range.SCHOOL);
            return Result.success().set("subjectIds", ListUtils.union(nonCombinedSubjectIds, combinedSubjectIds))
                    .set("students", students);
        }

    }
}
