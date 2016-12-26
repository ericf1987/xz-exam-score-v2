package com.xz.examscore.api.server.customization.examAlliance;

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
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by fengye on 2016/12/25.
 */
@Function(description = "联考项目-前百分段名平均分", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class CollegeEntryLevelRateAnalysis implements Server{

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    StudentService studentService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        List<Map<String, Object>> result = new ArrayList<>();
        List<String> entryLevelKey = collegeEntryLevelService.getEntryLevelKey(projectId);
        projectSchools.forEach(projectSchool -> {
            String schoolId = projectSchool.getString("school");
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            Range schoolRange = Range.school(schoolId);
            Target projectTarget = Target.project(projectId);
            entryLevelKey.forEach(key -> {
                Map<String, Object> schoolMap = new HashMap<>();
                int count = collegeEntryLevelService.getEntryLevelStudentCount(
                        projectId, schoolRange, projectTarget, key
                );
                int totalCount = studentService.getStudentCount(projectId, schoolRange, projectTarget);
                schoolMap.put("schoolName", schoolName);
                schoolMap.put("level", key);
                schoolMap.put("count", count);
                schoolMap.put("rate", DoubleUtils.round((double)count / totalCount, true));
                result.add(schoolMap);
            });
        });

        return Result.success().set("schoolCollegeEntryLevel", result);
    }
}
