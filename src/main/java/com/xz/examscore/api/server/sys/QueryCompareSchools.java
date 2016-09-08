package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 查询系统推荐的默认对比学校（8所学校）
 *
 * @author zhaorenwu
 */
@SuppressWarnings("unchecked")
@Function(description = "查询指定区域默认对比学校", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "city", type = Type.String, description = "地市编号", required = true),
        @Parameter(name = "area", type = Type.String, description = "区县编号", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "当前学校id", required = false),
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
        String city = param.getString("city");
        String area = param.getString("area");
        int limit = param.getInteger("limit");

        // 加入本校
        Set schoolSet = new LinkedHashSet();
        if (StringUtil.isNotBlank(schoolId)) {
            schoolSet.add(schoolId);
        }

        // 加入同区县学校
        if (StringUtil.isNotBlank(area)) {
            List<String> areaSchoolIds = schoolService.getProjectSchoolIds(projectId, area);
            schoolSet.addAll(areaSchoolIds);
        } else {
            // 加入同地市学校
            List<String> citySchoolIds = schoolService.getProjectSchoolIds(projectId, city);
            schoolSet.addAll(citySchoolIds);
        }

        if (schoolSet.size() >= limit) {
            return Result.success().set("schools", new ArrayList<>(schoolSet).subList(0, limit));
        } else {
            return Result.success().set("schools", new ArrayList<>(schoolSet));
        }
    }
}
