package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xz.api.server.project.ProjectSubjectAnalysis.getSubjectAnalysis;

/**
 * 学校成绩-学科分析
 *
 * @author zhaorenwu
 */

@Function(description = "学校成绩-学科分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolSubjectAnalysis implements Server {

    @Autowired
    ClassService classService;

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
        String schoolId = param.getString("schoolId");

        // 班级学科分析
        List<Map<String, Object>> classsSubjectMaps = getClassSubjectAnalysis(projectId, schoolId);

        // 学校学科分析
        Range range = Range.school(schoolId);
        Map<String, Object> schoolSubjectMaps = getSubjectAnalysis(projectId, range, studentService, averageService,
                subjectService, subjectRateService, fullScoreService, tScoreService);

        return Result.success().set("schools", schoolSubjectMaps).set("classes", classsSubjectMaps);
    }

    private List<Map<String, Object>> getClassSubjectAnalysis(String projectId, String schoolId) {
        List<Map<String, Object>> classsSubjectMaps = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            String classId = listClass.getString("class");
            String name = listClass.getString("name");

            Range range = Range.clazz(classId);
            Map<String, Object> subjectAnalysis = getSubjectAnalysis(projectId, range, studentService, averageService,
                    subjectService, subjectRateService, fullScoreService, tScoreService);

            subjectAnalysis.put("className", name);
            classsSubjectMaps.add(subjectAnalysis);
        }

        classsSubjectMaps.sort((o1, o2) -> ((String) o1.get("className")).compareTo(((String) o2.get("className"))));
        return classsSubjectMaps;
    }

}
