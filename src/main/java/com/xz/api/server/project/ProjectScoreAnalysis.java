package com.xz.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总体成绩-分数分析
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-分数分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectScoreAnalysis implements Server {

    public static Logger LOG = LoggerFactory.getLogger(ProjectScoreAnalysis.class);

    @Autowired
    SchoolService schoolService;

    @Autowired
    StudentService studentService;

    @Autowired
    MinMaxScoreService minMaxScoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    StdDeviationService stdDeviationService;

    @Autowired
    ScoreLevelService scoreLevelService;

    @Autowired
    TargetService targetService;

    @Autowired
    RangeService rangeService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> schoolStats = getSchoolStats(projectId, subjectId, schoolIds);
        Map<String, Object> totalStat = getProjectTotalStats(projectId, subjectId);

        return Result.success().set("totalStats", totalStat).set("schoolStats", schoolStats);
    }

    // 学校分数分析
    private List<Map<String, Object>> getSchoolStats(String projectId, String subjectId, String[] schoolIds) {
        List<Map<String, Object>> schoolStats = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.queryExamSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                return null;
            }

            Range range = Range.school(schoolId);
            Target target = targetService.getTarget(projectId, subjectId);
            Map<String, Object> schoolMap = getScoreAnalysisStatInfo(projectId, range, target,
                    studentService, minMaxScoreService, averageService, stdDeviationService, scoreLevelService);
            schoolMap.put("schoolName", schoolName);

            schoolStats.add(schoolMap);
        }

        return schoolStats;
    }

    // 项目整体分数分析
    private Map<String, Object> getProjectTotalStats(String projectId, String subjectId) {
        Range range = rangeService.queryProvinceRange(projectId);
        Target target = targetService.getTarget(projectId, subjectId);

        return getScoreAnalysisStatInfo(projectId, range, target, studentService, minMaxScoreService,
                averageService, stdDeviationService, scoreLevelService);
    }

    public static Map<String, Object> getScoreAnalysisStatInfo(String projectId, Range range, Target target,
                                                               StudentService studentService,
                                                               MinMaxScoreService minMaxScoreService,
                                                               AverageService averageService,
                                                               StdDeviationService stdDeviationService,
                                                               ScoreLevelService scoreLevelService) {
        Map<String, Object> statMap = new HashMap<>();

        // 考生人数
        int studentCount = studentService.getStudentCount(projectId, range, target);
        statMap.put("studentCount", studentCount);

        // 最高分与最低分
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, range, target);
        double minScore = minMaxScore[0];
        double maxScore = minMaxScore[1];
        statMap.put("minScore", minScore);
        statMap.put("maxScore", maxScore);

        // 平均分
        double avgScore = averageService.getAverage(projectId, range, target);
        statMap.put("avgScore", avgScore);

        // 标准差
        double stdDeviation = stdDeviationService.getStdDeviation(projectId, range, target);
        statMap.put("stdDeviation", stdDeviation);

        // 三率
        Map<String, Double> scoreLevelRate = scoreLevelService.getScoreLevelRate(projectId, range, target);
        for (String levelName : scoreLevelRate.keySet()) {
            statMap.put(levelName, scoreLevelRate.get(levelName));
        }

        statMap.put("target", target);
        return statMap;
    }
}
