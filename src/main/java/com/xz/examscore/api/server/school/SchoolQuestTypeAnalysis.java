package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.FullScoreService;
import com.xz.examscore.services.QuestTypeScoreService;
import com.xz.examscore.services.QuestTypeService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectQuestTypeAnalysis.getQuestTypeAnalysis;
import static com.xz.examscore.api.server.sys.QueryExamClasses.getFullClassName;

/**
 * 学校成绩-试卷题型分析
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-试卷题型分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolQuestTypeAnalysis implements Server {

    @Autowired
    ClassService classService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        List<Map<String, Object>> classQuestTypeAnalysis = getClassQuestTypeAnalysis(projectId, subjectId, schoolId);
        List<Map<String, Object>> schoolQuestTypeAnalysis = getSchoolQuestTypeAnalysis(projectId, subjectId, schoolId);

        return Result.success()
                .set("schools", schoolQuestTypeAnalysis)
                .set("classes", classQuestTypeAnalysis)
                .set("hasHeader", !(schoolQuestTypeAnalysis.isEmpty() || classQuestTypeAnalysis.isEmpty()));
    }

    // 班级试题分析
    private List<Map<String, Object>> getClassQuestTypeAnalysis(String projectId, String subjectId, String schoolId) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            Map<String, Object> map = new HashMap<>();
            String classId = listClass.getString("class");
            map.put("classId", classId);
            map.put("className", getFullClassName(listClass));

            Range range = Range.clazz(classId);
            List<Map<String, Object>> questTypes = getQuestTypeAnalysis(projectId, subjectId, range,
                    questTypeService, fullScoreService, questTypeScoreService);
            map.put("questTypes", questTypes);

            list.add(map);
        }

        list.sort((o1, o2) -> ((String) o1.get("className")).compareTo(((String) o2.get("className"))));
        return list;
    }

    // 学校试题分析
    private List<Map<String, Object>> getSchoolQuestTypeAnalysis(String projectId, String subjectId, String schoolId) {
        Range range = Range.school(schoolId);
        return getQuestTypeAnalysis(projectId, subjectId, range,
                questTypeService, fullScoreService, questTypeScoreService);
    }
}
