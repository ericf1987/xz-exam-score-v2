package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.services.AverageService;
import com.xz.services.ClassService;
import com.xz.services.QuestService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.api.server.project.ProjectSubjectiveAnalysis.getSubjectiveAnalysis;
import static com.xz.api.server.sys.QueryExamClasses.getFullClassName;

/**
 * 学校成绩-主观题分析
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-主观题分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolSubjectiveAnalysis implements Server {

    @Autowired
    ClassService classService;

    @Autowired
    QuestService questService;

    @Autowired
    AverageService averageService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        List<Map<String, Object>> classSubjectiveAnalysis = getClassSubjectiveAnalysis(projectId, subjectId, schoolId);
        List<Map<String, Object>> schoolSubjectiveAnalysis = getSchoolSubjectiveAnalysis(projectId, subjectId, schoolId);

        return Result.success().set("schools", schoolSubjectiveAnalysis).set("classes", classSubjectiveAnalysis);
    }

    // 班级主观题分析
    private List<Map<String, Object>> getClassSubjectiveAnalysis(String projectId, String subjectId, String schoolId) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            Map<String, Object> map = new HashMap<>();
            String classId = listClass.getString("class");
            map.put("className", getFullClassName(listClass));

            Range range = Range.clazz(classId);
            List<Map<String, Object>> subjectives = getSubjectiveAnalysis(projectId, subjectId, range,
                    questService, averageService);
            map.put("subjectives", subjectives);

            list.add(map);
        }

        list.sort((o1, o2) -> ((String) o1.get("className")).compareTo(((String) o2.get("className"))));
        return list;

    }

    // 学校主观题分析
    private List<Map<String, Object>> getSchoolSubjectiveAnalysis(String projectId, String subjectId, String schoolId) {
        Range range = Range.school(schoolId);
        return getSubjectiveAnalysis(projectId, subjectId, range,
                questService, averageService);
    }
}
