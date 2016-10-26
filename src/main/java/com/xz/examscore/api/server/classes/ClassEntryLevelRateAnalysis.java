package com.xz.examscore.api.server.classes;

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
 * @author by fengye on 2016/10/26.
 */
@Function(description = "班级成绩-班级本科上线率", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级ID", required = true)
})
@Service
public class ClassEntryLevelRateAnalysis implements Server {

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");
        Map<String, Object> result = new HashMap<>();
        int totalCount = 0;
        //获取班级参考学生人数
        int studentCount = studentService.getStudentCount(projectId, Range.clazz(classId), Target.project(projectId));
        String[] entryLevelKey = collegeEntryLevelService.getEntryLevelKey(projectId);
        List<Map<String, Object>> onlineRate = new ArrayList<>();
        for (String key : entryLevelKey) {
            Map<String, Object> map = new HashMap<>();
            int onlineCount = collegeEntryLevelService.getEntryLevelStudentCount(projectId, Range.clazz(classId), Target.project(projectId), key);
            double rate = (double) onlineCount / studentCount;
            String onlineDesc = collegeEntryLevelService.getEntryKeyDesc(key);
            List<Map<String, Object>> onlineStudents = paddingStudentInfo(projectId,
                    collegeEntryLevelService.getEntryLevelStudentByKey(projectId, Range.clazz(classId), Target.project(projectId), key));
            map.put("onlineCount", onlineCount);
            map.put("onlineDesc", onlineDesc);
            map.put("rate", rate);
            map.put("onlineStudents", onlineStudents);
            totalCount += onlineCount;
            onlineRate.add(map);
        }
        result.put("studentCount", studentCount);
        result.put("onlineRate", onlineRate);
        result.put("outlineRate", DoubleUtils.round((double) (studentCount - totalCount) / studentCount, true));
        return Result.success().set("classOnlineRate", result);
    }

    public List<Map<String, Object>> paddingStudentInfo(String projectId, List<Document> students) {
        List<Map<String, Object>> studentList = new ArrayList<>();
        students.forEach(student -> {
            Map<String, Object> map = new HashMap<>();
            String name = studentService.findStudent(projectId, student.getString("student")).getString("name");
            map.put("name", name);
            map.put("totalScore", student.getDouble("totalScore"));
            map.put("dValue", student.getDouble("dValue"));
            map.put("rank", student.getInteger("rank"));
            studentList.add(map);
        });
        return studentList;
    }
}
