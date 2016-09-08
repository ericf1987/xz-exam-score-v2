package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.ProjectConfigService;
import com.xz.examscore.services.SubjectService;
import com.xz.examscore.services.TopAverageService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectHighSegmentAnalysis.getHighSegmentAnalysis;
import static com.xz.examscore.api.server.sys.QueryExamClasses.getFullClassName;

/**
 * 学校成绩-高分段竞争力分析
 *
 * @author zhaorenwu
 */

@Function(description = "学校成绩-高分段竞争力分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "percent", type = Type.Decimal, description = "总分前百分比", required = false, defaultValue = "0.3"),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class SchoolHighSegmentAnalysis implements Server {

    @Autowired
    ClassService classService;

    @Autowired
    TopAverageService topAverageService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        //获取高分段
        ProjectConfig projectConfig =  projectConfigService.getProjectConfig(projectId);
        double percent = projectConfig.getHighScoreRate();

        List<Map<String, Object>> classHighSegmentAnalysis =
                getClassHighSegmentAnalysis(projectId, schoolId, percent, authSubjectIds);
        List<Map<String, Object>> schoolHighSegmentAnalysis =
                getSchoolHighSegmentAnalysis(projectId, schoolId, percent, authSubjectIds);

        return Result.success()
                .set("schools", schoolHighSegmentAnalysis)
                .set("classes", classHighSegmentAnalysis)
                .set("hasHeader", true);
    }

    // 班级高分段竞争力分析
    private List<Map<String, Object>> getClassHighSegmentAnalysis(
            String projectId, String schoolId, double percent, String[] authSubjectIds) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            Map<String, Object> map = new HashMap<>();
            String classId = listClass.getString("class");
            map.put("classId", classId);
            map.put("className", getFullClassName(listClass));

            Range range = Range.clazz(classId);
            List<Map<String, Object>> subjects = getHighSegmentAnalysis(projectId, range, percent,
                    authSubjectIds, subjectService, topAverageService);
            map.put("subjects", subjects);

            list.add(map);
        }

        list.sort((o1, o2) -> ((String) o1.get("className")).compareTo(((String) o2.get("className"))));
        return list;
    }

    // 学校高分段竞争力分析
    private List<Map<String, Object>> getSchoolHighSegmentAnalysis(
            String projectId, String schoolId, double percent, String[] authSubjectIds) {
        Range range = Range.school(schoolId);
        return getHighSegmentAnalysis(projectId, range, percent, authSubjectIds, subjectService, topAverageService);
    }
}
