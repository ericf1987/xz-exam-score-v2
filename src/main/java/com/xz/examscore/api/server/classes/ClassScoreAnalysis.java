package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectScoreAnalysis.getScoreAnalysisStatInfo;
import static com.xz.examscore.api.server.project.ProjectTopStudentStat.filterSubject;

/**
 * 班级成绩-分数分析
 *
 * @author zhaorenwu
 */
@Function(description = "班级成绩-分数分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID，不指定则查询出所有考试科目", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ClassScoreAnalysis implements Server {

    @Autowired
    StudentService studentService;

    @Autowired
    MinMaxScoreService minMaxScoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    StdDeviationService stdDeviationService;

    @Autowired
    PassAndUnPassService passAndUnPassService;

    @Autowired
    ScoreLevelService scoreLevelService;

    @Autowired
    RankPositionService rankPositionService;

    @Autowired
    OverAverageService overAverageService;

    @Autowired
    TargetService targetService;

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String classId = param.getString("classId");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        List<String> subjectIds = new ArrayList<>();
        if (StringUtil.isBlank(subjectId)) {
            subjectIds = new ArrayList<>(subjectService.querySubjects(projectId));
        } else {
            subjectIds.add(subjectId);
        }
        subjectIds = filterSubject(subjectIds, authSubjectIds);
        subjectIds.sort(String::compareTo);


        List<Map<String, Object>> classSubjectStatList = new ArrayList<>();
        for (String _subjectId : subjectIds) {
            String subjectName = SubjectService.getSubjectName(_subjectId);

            Map<String, Object> classSubjectStatMap = getClassStats(projectId, _subjectId, classId);
            classSubjectStatMap.put("subjectName", subjectName);
            classSubjectStatList.add(classSubjectStatMap);
        }

        Map<String, Object> classProjectStatMap = getClassStats(projectId, null, classId);
        return Result.success()
                .set("subjects", classSubjectStatList)
                .set("totals", classProjectStatMap)
                .set("hasHeader", true);
    }

    // 获取学校班级分数分析统计
    private Map<String, Object> getClassStats(String projectId, String subjectId, String classId) {
        Range range = Range.clazz(classId);
        Target target = targetService.getTarget(projectId, subjectId);

        return getScoreAnalysisStatInfo(projectId, range, target,
                studentService, minMaxScoreService, averageService, stdDeviationService, scoreLevelService,
                passAndUnPassService, rankPositionService, overAverageService);
    }
}
