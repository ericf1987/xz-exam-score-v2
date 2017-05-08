package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.CollegeEntryLevelService;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/12/28.
 */
@Function(description = "联考项目-一本入围比例核算", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class ToBeEntryLevelAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    StudentService studentService;

    @Autowired
    ProvinceService provinceService;

    public static final String ENTRY_LEVEL_KEY = "TWO";

    public static final double ENTRY_LEVEL_SCORE_TO_BE = 15d;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        Range projectRange = Range.province(provinceService.getProjectProvince(projectId));
        Target target = Target.project(projectId);
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);

        //获取总体
        Map<String, Object> provinceData = getToBeEntryLevel(projectId, projectRange, target);
        List<Map<String, Object>> schoolData = new ArrayList<>();
        projectSchools.forEach(school -> {
            Map<String, Object> map = getToBeEntryLevel(projectId, Range.school(school.getString("school")), target);
            schoolData.add(map);
        });

        return Result.success().set("provinceData", provinceData).set("schoolData", schoolData);
    }

    public Map<String, Object> getToBeEntryLevel(String projectId, Range range, Target target) {
        //参考学生数
        int studentCount = studentService.getStudentCount(projectId, range, target);
        //一本上线学生数
        int oneCount = collegeEntryLevelService.getEntryLevelStudentCount(projectId, range, target, "ONE");
        //获取临界生的分数线
        List<Document> entryLevelDoc = collegeEntryLevelService.getEntryLevelDoc(projectId);
        double entryLevelOne = entryLevelDoc.stream().filter(doc -> doc.getString("level").equals("ONE"))
                .mapToDouble(doc -> doc.getDouble("score")).sum();
        //比一本线低15分为临界分数线
        double scoreLine = entryLevelOne - ENTRY_LEVEL_SCORE_TO_BE;
        ArrayList<Document> studentByKey = collegeEntryLevelService.getEntryLevelStudentByKey(projectId, range, target, ENTRY_LEVEL_KEY);
        List<Document> requiredStudent = studentByKey.stream().filter(student -> student.getDouble("totalScore") >= scoreLine).collect(Collectors.toList());
        //临界生人数
        int requiredCount = requiredStudent.size();
        //入围人数
        int count_to_be = requiredCount + oneCount;
        //一本上线率
        double oneRate = DoubleUtils.round((double) oneCount / studentCount);
        //入围比率
        double onlineRate_to_be = DoubleUtils.round((double) count_to_be / studentCount);
        //增率
        double uprate = DoubleUtils.round((double) requiredCount / studentCount);
        Map<String, Object> map = new HashMap<>();
        String schoolName = schoolService.getSchoolName(projectId, range.getId());
        map.put("schoolName", StringUtils.isEmpty(schoolName) ? "总体" : schoolName);
        map.put("studentCount", studentCount);
        map.put("oneCount", oneCount);
        map.put("oneRate", oneRate);
        map.put("requiredCount", requiredCount);
        map.put("count_to_be", count_to_be);
        map.put("onlineRate_to_be", onlineRate_to_be);
        map.put("uprate", uprate);
        return map;
    }
}
