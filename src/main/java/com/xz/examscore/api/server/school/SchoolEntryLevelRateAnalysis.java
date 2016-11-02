package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.CollegeEntryLevelService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/10/25.
 */
@Function(description = "学校成绩-各班本科上线率", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = true)
})
@Service
public class SchoolEntryLevelRateAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        List<Document> classDocs = classService.listClasses(projectId, schoolId);
        String[] entryLevelKey = collegeEntryLevelService.getEntryLevelKey(projectId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Document doc : classDocs) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> onlineRate = new ArrayList<>();
            String classId = doc.getString("class");
            Target projectTarget = Target.project(projectId);
            int studentCount = studentService.getStudentCount(projectId, Range.clazz(classId), projectTarget);
            for(String key : entryLevelKey){
                Map<String,Object> m = new HashMap<>();
                int onlineCount = collegeEntryLevelService.getEntryLevelStudentCount(projectId, Range.clazz(classId), projectTarget, key);
                double rate = DoubleUtils.round((double) onlineCount / studentCount, true);
                m.put("onlineCount", onlineCount);
                m.put("onlineDesc", collegeEntryLevelService.getEntryKeyDesc(key));
                m.put("rate", rate);
                onlineRate.add(m);
            }
            map.put("className", doc.getString("name"));
            map.put("studentCount", studentCount);
            map.put("onlineRate", onlineRate);
            map.put("classId", classId);
            result.add(map);
        }
        return Result.success().set("classOnlineRate", result);
    }
}
