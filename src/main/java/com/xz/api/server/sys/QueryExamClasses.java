package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import com.xz.services.ClassService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 查询考试班级列表
 *
 * @author zhaorenwu
 */

@Function(description = "查询考试班级列表", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false),
}, result = @ResultInfo(listProperties =
@ListProperty(name = "classes", description = "考试班级列表", properties = {
        @Property(name = "project", type = Type.String, description = "考试项目id"),
        @Property(name = "school", type = Type.String, description = "学校id"),
        @Property(name = "class", type = Type.String, description = "班级id"),
        @Property(name = "name", type = Type.String, description = "班级名称"),
        @Property(name = "grade", type = Type.String, description = "年级")
})))
@Service
public class QueryExamClasses implements Server {

    @Autowired
    ClassService classService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");

        List<Document> listClasses = classService.listClasses(projectId, schoolId);
        listClasses.sort((o1, o2) -> o1.getString("name").compareTo(o2.getString("name")));
        return Result.success().set("classes", listClasses);
    }
}
