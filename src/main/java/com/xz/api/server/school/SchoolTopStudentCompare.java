package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.ClassService;
import com.xz.services.TopStudentListService;
import com.xz.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学校成绩-尖子生对比分析
 *
 * @author zhaorenwu
 */
@Function(description = "学校成绩-尖子生对比分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolTopStudentCompare implements Server {

    @Autowired
    ClassService classService;

    @Autowired
    TopStudentListService topStudentListService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");

        List<Map<String, Object>> classTopStudentRates =  getClassTopStudentRates(projectId, schoolId);
        return Result.success().set("classes", classTopStudentRates);
    }

    // 班级在学校中的尖子生比率
    private List<Map<String, Object>> getClassTopStudentRates(String projectId, String schoolId) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        for (Document listClass : listClasses) {
            Map<String, Object> map = new HashMap<>();
            String classId = listClass.getString("class");
            String name = listClass.getString("name");

            map.put("className", name);

            Range rankRange = Range.school(schoolId);
            Range compareRange = Range.clazz(classId);
            Target target = Target.project(projectId);
            int topStudentTotalCount = topStudentListService.getTopStudentTotalCount(projectId, rankRange);
            int topStudentCount = topStudentListService.getTopStudentCount(projectId, rankRange,
                    compareRange, target, 1, topStudentTotalCount);

            map.put("count", topStudentCount);
            map.put("rate", DoubleUtils.round(topStudentTotalCount == 0 ?
                    0 : topStudentCount * 1.0 / topStudentTotalCount, true));

            list.add(map);
        }

        list.sort((o1, o2) -> ((String) o1.get("className")).compareTo((String) o2.get("className")));
        return list;
    }
}
