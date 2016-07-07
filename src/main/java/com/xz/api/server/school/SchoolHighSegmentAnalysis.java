package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.services.ClassService;
import com.xz.services.SubjectService;
import com.xz.services.TopAverageService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.api.server.project.ProjectHighSegmentAnalysis.getHighSegmentAnalysis;
import static com.xz.api.server.sys.QueryExamClasses.getFullClassName;

/**
 * 学校成绩-高分段竞争力分析
 *
 * @author zhaorenwu
 */

@Function(description = "学校成绩-高分段竞争力分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "percent", type = Type.Decimal, description = "总分前百分比", required = false, defaultValue = "0.3")
})
@Service
public class SchoolHighSegmentAnalysis implements Server {

    @Autowired
    ClassService classService;

    @Autowired
    TopAverageService topAverageService;

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        double percent = param.getDouble("percent");

        List<Map<String, Object>> classHighSegmentAnalysis =
                getClassHighSegmentAnalysis(projectId, schoolId, percent);
        List<Map<String, Object>> schoolHighSegmentAnalysis =
                getSchoolHighSegmentAnalysis(projectId, schoolId, percent);

        return Result.success()
                .set("schools", schoolHighSegmentAnalysis)
                .set("classes", classHighSegmentAnalysis)
                .set("hasHeader", true);
    }

    // 班级高分段竞争力分析
    private List<Map<String, Object>> getClassHighSegmentAnalysis(String projectId, String schoolId, double percent) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            Map<String, Object> map = new HashMap<>();
            String classId = listClass.getString("class");
            map.put("className", getFullClassName(listClass));

            Range range = Range.clazz(classId);
            List<Map<String, Object>> subjects = getHighSegmentAnalysis(projectId, range, percent,
                    subjectService, topAverageService);
            map.put("subjects", subjects);

            list.add(map);
        }

        list.sort((o1, o2) -> ((String) o1.get("className")).compareTo(((String) o2.get("className"))));
        return list;
    }

    // 学校高分段竞争力分析
    private List<Map<String, Object>> getSchoolHighSegmentAnalysis(String projectId, String schoolId, double percent) {
        Range range = Range.school(schoolId);
        return getHighSegmentAnalysis(projectId, range, percent, subjectService, topAverageService);
    }
}
