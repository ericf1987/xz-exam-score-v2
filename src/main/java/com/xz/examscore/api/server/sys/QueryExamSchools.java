package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.*;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.SchoolService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 查询考试学校列表
 *
 * @author zhaorenwu
 */

@Function(description = "查询考试学校列表", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "area", type = Type.String, description = "地区编码（参见 t_sys_city 表）", required = false),
}, result = @ResultInfo(listProperties =
@ListProperty(name = "schools", description = "考试学校列表", properties = {
        @Property(name = "project", type = Type.String, description = "考试项目id"),
        @Property(name = "school", type = Type.String, description = "学校id"),
        @Property(name = "name", type = Type.String, description = "学校名称"),
})))
@Service
public class QueryExamSchools implements Server {

    @Autowired
    SchoolService schoolService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String area = param.getString("area");

        List<Document> examSchools = schoolService.getProjectSchools(projectId, area);

        Collections.sort(examSchools, (Document d1, Document d2) -> {
            String n1 = d1.getString("name");
            String n2 = d2.getString("name");
            return n2.compareTo(n1);
        });
        return Result.success().set("schools", examSchools);
    }
}
