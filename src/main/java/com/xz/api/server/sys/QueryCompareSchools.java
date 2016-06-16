package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 查询系统推荐的默认对比学校（10所学校）
 *
 * @author zhaorenwu
 */
@SuppressWarnings("unchecked")
@Function(description = "查询系统推荐的默认对比学校", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "limit", type = Type.Integer, description = "获取的学校数量", required = false, defaultValue = "8")
})
@Service
public class QueryCompareSchools implements Server {

    @Autowired
    SchoolService schoolService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        int limit = param.getInteger("limit");

        Document school = schoolService.findSchool(projectId, schoolId);
        if (school == null) {
            return Result.fail("找不到指定的考试学校'" + schoolId + "'");
        }

        String area = school.getString("area");
        String city = school.getString("city");

        // 加入本校
        Set schoolSet = new LinkedHashSet();
        schoolSet.add(schoolId);

        // 加入同区县学校
        List<String> areaSchoolIds = schoolService.getProjectSchoolIds(projectId, area);
        schoolSet.addAll(areaSchoolIds);
        if (schoolSet.size() >= limit) {
            return Result.success().set("schools", new ArrayList<>(schoolSet).subList(0, limit));
        }

        // 加入同地市学校
        List<String> citySchoolIds = schoolService.getProjectSchoolIds(projectId, city);
        schoolSet.addAll(citySchoolIds);
        if (schoolSet.size() >= limit) {
            return Result.success().set("schools", new ArrayList<>(schoolSet).subList(0, limit));
        }

        // 加入同项目学校
        List<String> projectSchoolIds = schoolService.getProjectSchoolIds(projectId, city);
        schoolSet.addAll(projectSchoolIds);
        if (schoolSet.size() >= limit) {
            return Result.success().set("schools", new ArrayList<>(schoolSet).subList(0, limit));
        }

        return Result.success().set("schools", new ArrayList<>(schoolSet));
    }
}
