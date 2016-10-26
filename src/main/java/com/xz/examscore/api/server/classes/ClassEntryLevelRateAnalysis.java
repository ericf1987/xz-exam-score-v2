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
import java.util.stream.Collectors;

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
        Range clazzRange = Range.clazz(classId);
        Target projectTarget = Target.project(projectId);
        int totalCount = 0;
        //获取班级参考学生人数
        int studentCount = studentService.getStudentCount(projectId, clazzRange, projectTarget);
        List<String> studentIds = studentService.getStudentIds(projectId, clazzRange, projectTarget);
        //本科录取的学生
        List<Document> entryLevelStudents = new ArrayList<>();
        String[] entryLevelKey = collegeEntryLevelService.getEntryLevelKey(projectId);
        List<Map<String, Object>> onlineRate = new ArrayList<>();
        Map<String, Object> outlineMap = new HashMap<>();
        for (String key : entryLevelKey) {
            Map<String, Object> map = new HashMap<>();
            int onlineCount = collegeEntryLevelService.getEntryLevelStudentCount(projectId, clazzRange, projectTarget, key);
            double rate = (double) onlineCount / studentCount;
            String onlineDesc = collegeEntryLevelService.getEntryKeyDesc(key);
            //将每一批次的本科录取学生添加至录取列表
            List<Document> part = collegeEntryLevelService.getEntryLevelStudentByKey(projectId, clazzRange, projectTarget, key);
            entryLevelStudents.addAll(part);
            List<String> onlineStudents = paddingStudentInfo(projectId,part);
            map.put("onlineCount", onlineCount);
            map.put("onlineDesc", onlineDesc);
            map.put("rate", rate);
            map.put("onlineStudents", onlineStudents);
            totalCount += onlineCount;
            onlineRate.add(map);
        }
        result.put("studentCount", studentCount);
        result.put("onlineRate", onlineRate);
        outlineMap.put("rate", DoubleUtils.round((double) (studentCount - totalCount) / studentCount, true));
        outlineMap.put("outlineCount", studentCount - totalCount);
        outlineMap.put("outlineStudents", getOutlintStudents(projectId, studentIds, entryLevelStudents));
        result.put("outlineRate", outlineMap);
        return Result.success().set("classOnlineRate", result);
    }

    private List<String> getOutlintStudents(String projectId, List<String> studentIds, List<Document> entryLevelStudents) {
        //取出所有本科录取学生ID
        List<String> onlineIds = entryLevelStudents.stream().map(id -> id.getString("student")).collect(Collectors.toList());
        studentIds.removeAll(onlineIds);
        return studentIds.stream().map(id -> studentService.findStudent(projectId, id).getString("name")).collect(Collectors.toList());
    }

    public List<String> paddingStudentInfo(String projectId, List<Document> students) {
        return students.stream().map(student -> studentService.findStudent(projectId, student.getString("student")).getString("name")).collect(Collectors.toList());
    }
}
