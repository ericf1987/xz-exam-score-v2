package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xz.api.server.project.ProjectScoreAnalysis.getScoreAnalysisStatInfo;

/**
 * 学校成绩-分数分析
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-分数分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolScoreAnalysis implements Server {

    @Autowired
    ClassService classService;

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
    TargetService targetService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        List<Map<String, Object>> classStats = getClassStats(projectId, subjectId, schoolId);
        Map<String, Object> schoolStats = getSchoolTotalStats(projectId, subjectId, schoolId);

        return Result.success().set("schools", schoolStats).set("classes", classStats);
    }

    // 获取学校班级分数分析统计
    private List<Map<String, Object>> getClassStats(String projectId, String subjectId, String schoolId) {
        List<Map<String, Object>> classStats = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            String classId = listClass.getString("class");
            String name = listClass.getString("name");

            Range range = Range.clazz(classId);
            Target target = targetService.getTarget(projectId, subjectId);
            Map<String, Object> schoolMap = getScoreAnalysisStatInfo(projectId, range, target,
                    studentService, minMaxScoreService, averageService, stdDeviationService, scoreLevelService,
                    passAndUnPassService, rankPositionService);
            schoolMap.put("className", name);

            classStats.add(schoolMap);
        }

        classStats.sort((o1, o2) -> ((String) o1.get("className")).compareTo(((String) o2.get("className"))));
        return classStats;
    }

    // 获取学校分数分析统计
    private Map<String, Object> getSchoolTotalStats(String projectId, String subjectId, String schoolId) {
        Range range = Range.school(schoolId);
        Target target = targetService.getTarget(projectId, subjectId);

        return getScoreAnalysisStatInfo(projectId, range, target,
                studentService, minMaxScoreService, averageService, stdDeviationService,
                scoreLevelService, passAndUnPassService, rankPositionService);
    }
}
