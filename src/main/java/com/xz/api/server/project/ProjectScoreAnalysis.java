package com.xz.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import com.xz.util.DoubleUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 总体成绩-分数分析
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-分数分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "isInCity", type = Type.String, description = "是否是城区学校", required = false),
        @Parameter(name = "isGovernmental", type = Type.String, description = "是否是公办学校", required = false),
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
    PassAndUnPassService passAndUnPassService;

    @Autowired
    RankPositionService rankPositionService;

    @Autowired
    OverAverageService overAverageService;

    @Autowired
    TargetService targetService;

    @Autowired
    RangeService rangeService;

    //根据标签过滤学校ID
    public String[] filterByTags(String projectId, String isIncity, String isGovernmental) {

        List<String> schools = schoolService.getSchoolsByTags(projectId, isIncity, isGovernmental).stream()
            .map(document -> document.getString("school")).collect(Collectors.toList());

        return schools.toArray(new String[schools.size()]);
    }

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String isIncity = param.getString("isInCity") != null ? param.getString("isInCity") : null;
        String isGovernmental = param.getString("isGovernmental") != null ? param.getString("isGovernmental") : null;
        String[] schoolIds = param.getStringValues("schoolIds");

        if(null == schoolIds || schoolIds.length == 0){
            schoolIds = filterByTags(projectId, isIncity, isGovernmental);
        }

        List<Map<String, Object>> schoolStats = getSchoolStats(projectId, subjectId, schoolIds);
        Map<String, Object> totalStat = getProjectTotalStats(projectId, subjectId);

        return Result.success()
                .set("totals", totalStat)
                .set("schools", schoolStats)
                .set("hasHeader", true);
    }

    // 学校分数分析
    private List<Map<String, Object>> getSchoolStats(String projectId, String subjectId, String[] schoolIds) {
        List<Map<String, Object>> schoolStats = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                continue;
            }

            Range range = Range.school(schoolId);
            Target target = targetService.getTarget(projectId, subjectId);
            Map<String, Object> schoolMap = getScoreAnalysisStatInfo(projectId, range, target,
                    studentService, minMaxScoreService, averageService, stdDeviationService, scoreLevelService,
                    passAndUnPassService, rankPositionService, overAverageService);
            schoolMap.put("schoolName", schoolName);

            schoolStats.add(schoolMap);
        }

        return schoolStats;
    }

    // 项目整体分数分析
    private Map<String, Object> getProjectTotalStats(String projectId, String subjectId) {
        Range range = rangeService.queryProvinceRange(projectId);
        Target target = targetService.getTarget(projectId, subjectId);

        return getScoreAnalysisStatInfo(projectId, range, target, studentService,
                minMaxScoreService, averageService, stdDeviationService, scoreLevelService,
                passAndUnPassService, rankPositionService, overAverageService);
    }

    public static Map<String, Object> getScoreAnalysisStatInfo(
            String projectId, Range range, Target target,
            StudentService studentService,
            MinMaxScoreService minMaxScoreService,
            AverageService averageService,
            StdDeviationService stdDeviationService,
            ScoreLevelService scoreLevelService,
            PassAndUnPassService passAndUnPassService,
            RankPositionService rankPositionService,
            OverAverageService overAverageService) {

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
        statMap.put("avgScore", DoubleUtils.round(avgScore));

        // 超均率
        double overAverage = overAverageService.getOverAverage(projectId, range, target);
        statMap.put("overAverage", DoubleUtils.round(overAverage, true));

        // 标准差
        double stdDeviation = stdDeviationService.getStdDeviation(projectId, range, target);
        statMap.put("stdDeviation", DoubleUtils.round(stdDeviation));

        // 三率
        List<Document> scoreLevelRate = scoreLevelService.getScoreLevelRate(projectId, range, target);
        for (Document document : scoreLevelRate) {
            document.put("rate", DoubleUtils.round(document.getDouble("rate"), true));
            statMap.put(document.getString("scoreLevel"), document);
        }

        // 全科及格率与不及格率
        double[] passAndUnPass = passAndUnPassService.getAllSubjectPassAndUnPass(projectId, range);
        statMap.put("allPassRate", DoubleUtils.round(passAndUnPass[0], true));
        statMap.put("allFailRate", DoubleUtils.round(passAndUnPass[1], true));

        // 中位数
        // 注意这里是从缓存中取出来的，所以不可以直接操作里面的内容，必须拷贝一份
        List<Document> rankPositions = new ArrayList<>(rankPositionService.getRankPositions(projectId, range, target));

        for (Document rankPosition : rankPositions) {
            rankPosition.put("score", DoubleUtils.round(rankPosition.getDouble("score")));
        }
        rankPositions.sort((o1, o2) -> o1.getDouble("position").compareTo(o2.getDouble("position")));
        statMap.put("rankPositions", rankPositions);

        statMap.put("target", target);
        return statMap;
    }
}
