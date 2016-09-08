package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.FullScoreService;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.sys.QueryExamClasses.getFullClassName;

/**
 * 学校成绩-知识点对比
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-知识点对比分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "pointId", type = Type.String, description = "知识点ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolPointCompare implements Server {

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    ClassService classService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String pointId = param.getString("pointId");
        String schoolId = param.getString("schoolId");

        List<Map<String, Object>> classes = new ArrayList<>();
        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            Map<String, Object> map = new HashMap<>();
            String classId = listClass.getString("class");
            map.put("classId", classId);
            map.put("className", getFullClassName(listClass));

            // 知识点得分率
            Target target = Target.point(pointId);
            double fullScore = fullScoreService.getFullScore(projectId, target);
            double average = averageService.getAverage(projectId, Range.clazz(classId), target);
            map.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : average / fullScore, true));
            map.put("fullScore", fullScore);

            classes.add(map);
        }

        classes.sort((o1, o2) -> ((String) o1.get("className")).compareTo(((String) o2.get("className"))));
        return Result.success().set("classes", classes);
    }
}
