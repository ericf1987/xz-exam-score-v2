package com.xz.examscore.api.server.project;

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
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总体成绩-学科分析
 *
 * @author zhaorenwu
 */

@Function(description = "总体成绩-学科分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectSubjectAnalysis implements Server {

    public static final Logger LOG = LoggerFactory.getLogger(ProjectSubjectAnalysis.class);

    @Autowired
    SchoolService schoolService;

    @Autowired
    StudentService studentService;

    @Autowired
    AverageService averageService;

    @Autowired
    RangeService rangeService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectRateService subjectRateService;

    @Autowired
    TScoreService tScoreService;

    @Autowired
    FullScoreService fullScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String[] schoolIds = param.getStringValues("schoolIds");

        // 学校学科分析
        List<Map<String, Object>> schoolSubjectMaps = getSchoolSubjectAnalysis(projectId, schoolIds);

        // 总体学科分析
        Range range = rangeService.queryProvinceRange(projectId);
        Map<String, Object> totalSubjectMap = getSubjectAnalysis(projectId, range, studentService, averageService,
                subjectService, subjectRateService, fullScoreService, tScoreService);

        return Result.success()
                .set("totals", totalSubjectMap)
                .set("schools", schoolSubjectMaps)
                .set("hasHeader", !((List) totalSubjectMap.get("subjects")).isEmpty());
    }

    private List<Map<String, Object>> getSchoolSubjectAnalysis(String projectId, String[] schoolIds) {
        List<Map<String, Object>> schoolSubjectMaps = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                continue;
            }

            Range range = Range.school(schoolId);
            Map<String, Object> subjectAnalysis = getSubjectAnalysis(projectId, range, studentService, averageService,
                    subjectService, subjectRateService, fullScoreService, tScoreService);

            subjectAnalysis.put("schoolName", schoolName);
            schoolSubjectMaps.add(subjectAnalysis);
        }

        return schoolSubjectMaps;
    }

    // 获取学科分析
    public static Map<String, Object> getSubjectAnalysis(String projectId, Range range,
                                                         StudentService studentService,
                                                         AverageService averageService,
                                                         SubjectService subjectService,
                                                         SubjectRateService subjectRateService,
                                                         FullScoreService fullScoreService,
                                                         TScoreService tScoreService) {
        Map<String, Object> subjectAnalysisMap = new HashMap<>();

        // 考试人数
        int studentCount = studentService.getStudentCount(projectId, range);
        subjectAnalysisMap.put("studentCount", studentCount);

        // 总分平均分
        double totalAvg = averageService.getAverage(projectId, range, Target.project(projectId));
        subjectAnalysisMap.put("totalAvg", DoubleUtils.round(totalAvg));

        // 科目分析
        Map<String, Document> subjectRateMap = subjectRateService.querySubjectRateMap(projectId, range);
        List<Map<String, Object>> subjectList = new ArrayList<>();
        List<String> subjects = subjectService.querySubjects(projectId);
        for (String subject : subjects) {
            Map<String, Object> map = new HashMap<>();
            Target target = Target.subject(subject);

            map.put("subjectId", subject);
            map.put("subjectName", SubjectService.getSubjectName(subject));

            double fullScore = fullScoreService.getFullScore(projectId, target);

            // 科目平均分与得分率
            double subjectAvg = averageService.getAverage(projectId, range, target);
            map.put("subjectAvg", DoubleUtils.round(subjectAvg));
            map.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : subjectAvg / fullScore, true));

            // 科目T值
            double tScore = tScoreService.queryTScore(projectId, target, range);
            map.put("tScore", DoubleUtils.round(tScore));

            // 科目贡献度
            Document subjectRate = subjectRateMap.get(subject);
            map.put("subjectRate", DoubleUtils.round(subjectRate == null ? 0 : subjectRate.getDouble("rate"), true));
            subjectList.add(map);
        }

        subjectAnalysisMap.put("subjects", subjectList);
        return subjectAnalysisMap;
    }
}
