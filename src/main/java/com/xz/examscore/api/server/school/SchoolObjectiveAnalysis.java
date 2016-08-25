package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.OptionMapService;
import com.xz.examscore.services.QuestDeviationService;
import com.xz.examscore.services.QuestService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectObjectiveAnalysis.getObjectiveAnalysis;
import static com.xz.examscore.api.server.sys.QueryExamClasses.getFullClassName;

/**
 * 学校成绩-客观题分析
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-客观题分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolObjectiveAnalysis implements Server {

    @Autowired
    ClassService classService;

    @Autowired
    QuestService questService;

    @Autowired
    QuestDeviationService questDeviationService;

    @Autowired
    OptionMapService optionMapService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        List<Map<String, Object>> classObjectiveAnalysis = getClassAnalysis(projectId, subjectId, schoolId);
        List<Map<String, Object>> schoolObjectiveAnalysis = getSchoolTotalAnalysis(projectId, subjectId, schoolId);

        return Result.success()
                .set("schools", schoolObjectiveAnalysis)
                .set("classes", classObjectiveAnalysis)
                .set("hasHeader", !schoolObjectiveAnalysis.isEmpty());
    }

    // 班级客观题分析
    private List<Map<String, Object>> getClassAnalysis(String projectId, String subjectId, String schoolId) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            Map<String, Object> map = new HashMap<>();
            String classId = listClass.getString("class");
            map.put("className", getFullClassName(listClass));

            Range range = Range.clazz(classId);
            List<Map<String, Object>> objectives = getObjectiveAnalysis(projectId, subjectId, range,
                    questService, optionMapService, questDeviationService);
            map.put("objectives", objectives);

            list.add(map);
        }

        list.sort((o1, o2) -> ((String) o1.get("className")).compareTo(((String) o2.get("className"))));
        return list;
    }

    // 学校客观题分数
    private List<Map<String, Object>> getSchoolTotalAnalysis(String projectId, String subjectId, String schoolId) {
        Range range = Range.school(schoolId);
        return getObjectiveAnalysis(projectId, subjectId, range,
                questService, optionMapService, questDeviationService);
    }
}
