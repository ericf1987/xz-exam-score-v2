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

        return Result.success().set("totals", totalSubjectMap).set("schools", schoolSubjectMaps);
    }

    private List<Map<String, Object>> getSchoolSubjectAnalysis(String projectId, String[] schoolIds) {
        List<Map<String, Object>> schoolSubjectMaps = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.queryExamSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                return null;
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
        subjectAnalysisMap.put("totalAvg", totalAvg);

        // 科目分析
        Map<String, Document> subjectRateMap = subjectRateService.querySubjectRateMap(projectId, range);
        List<Map<String, Object>> subjectList = new ArrayList<>();
        List<String> subjects = subjectService.querySubjects(projectId);
        for (String subject : subjects) {
            Map<String, Object> map = new HashMap<>();
            Target target = Target.subject(subject);

            map.put("subjectId", subject);
            map.put("subjectName", SubjectService.getSubjectName(subject).toString());

            double fullScore = fullScoreService.getFullScore(projectId, target);

            // 科目平均分与得分率
            double subjectAvg = averageService.getAverage(projectId, range, target);
            map.put("subjectAvg", subjectAvg);
            map.put("subjectRate", fullScore == 0 ? 0 : subjectAvg / fullScore);

            // 科目T值
            map.put("tScore", tScoreService.queryTScore(projectId, target, range));

            // 科目贡献度
            Document subjectRate = subjectRateMap.get(subject);
            map.put("subjectRate", subjectRate == null ? 0 : subjectRate.getDouble("rate"));
            subjectList.add(map);
        }

        subjectAnalysisMap.put("subjects", subjectList);
        return subjectAnalysisMap;
    }
}
