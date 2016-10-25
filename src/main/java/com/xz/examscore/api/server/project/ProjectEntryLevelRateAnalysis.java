package com.xz.examscore.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
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
@Function(description = "总体成绩-各校本科上线率", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
})
@Service
public class ProjectEntryLevelRateAnalysis implements Server {
    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    StudentService studentService;

    @Autowired
    SchoolService schoolService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<Map<String, Object>> result = new ArrayList<>();
        String[] entryLevelKey = collegeEntryLevelService.getEntryLevelKey(projectId);
        //学校列表
        List<Document> schoolDocs = schoolService.getProjectSchools(projectId);
        for (String key : entryLevelKey) {
            for (Document doc : schoolDocs) {
                Map<String, Object> map = new HashMap<>();
                String schoolId = doc.getString("school");
                int studentCount = studentService.getStudentCount(projectId, Range.school(schoolId));
                int onlineCount = collegeEntryLevelService.getEntryLevelStudentCount(projectId, Range.school(schoolId), Target.project(projectId), key);
                double rate = DoubleUtils.round((double) onlineCount / studentCount, true);
                map.put("schoolName", doc.getString("name"));
                map.put("studentCount", studentCount);
                map.put("onlineCount", onlineCount);
                map.put("rate", rate);
                map.put("onlineDesc", collegeEntryLevelService.getEntryKeyDesc(key));
                result.add(map);
            }
        }
        return Result.success().set("schoolOnlineRate", result);
    }
}
