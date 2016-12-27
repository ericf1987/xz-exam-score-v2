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
import com.xz.examscore.services.ProvinceService;
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
@Function(description = "联考项目-各校本科人数及上线率", parameters = {
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

    @Autowired
    ProvinceService provinceService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        List<Map<String, Object>> schoolData = new ArrayList<>();
        List<String> entryLevelKey = Arrays.asList("ONE", "TWO");
        projectSchools.forEach(projectSchool -> {
            String schoolId = projectSchool.getString("school");
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            Range schoolRange = Range.school(schoolId);
            Target projectTarget = Target.project(projectId);
            Map<String, Object> schoolMap = getCollegeEntryLevelRate(projectId, entryLevelKey, schoolId, schoolName, schoolRange, projectTarget);
            schoolData.add(schoolMap);
        });

        Map<String, Object> projectData = new HashMap<>();
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Map<String, Object> projectMap = getCollegeEntryLevelRate(projectId, entryLevelKey, projectId, "总体", provinceRange, Target.project(projectId));
        projectData.putAll(projectMap);

        return Result.success().set("schoolCollegeEntryLevel", schoolData).set("projectCollegeEntryLevel", projectData);
    }

    public Map<String, Object> getCollegeEntryLevelRate(String projectId, List<String> entryLevelKey, String schoolId, String schoolName, Range schoolRange, Target projectTarget) {
        //各校参考人数
        int totalCount = studentService.getStudentCount(projectId, schoolRange, projectTarget);
        Map<String, Object> schoolMap = new HashMap<>();
        schoolMap.put("schoolId", schoolId);
        schoolMap.put("schoolName", schoolName);
        int onlineCount = 0;
        List<Map<String, Object>> entryLevelList = new ArrayList<>();
        for(String key : entryLevelKey){
            Map<String, Object> entryLevelRateMap = new HashMap<>();
            int count = collegeEntryLevelService.getEntryLevelStudentCount(
                    projectId, schoolRange, projectTarget, key
            );
            onlineCount += count;

            entryLevelRateMap.put("level", key);
            entryLevelRateMap.put("count", onlineCount);
            entryLevelRateMap.put("rate", DoubleUtils.round((double)count / totalCount, true));
            entryLevelList.add(entryLevelRateMap);
        }
        schoolMap.put("entryLevelList", entryLevelList);
        return schoolMap;
    }
}
