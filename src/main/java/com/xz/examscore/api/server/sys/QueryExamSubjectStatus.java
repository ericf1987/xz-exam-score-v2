package com.xz.examscore.api.server.sys;

import com.alibaba.fastjson.JSONArray;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.*;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.intclient.InterfaceAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 查询考试科目发布状态
 *
 * @author zhaorenwu
 */
@Function(description = "查询考试科目发布状态", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "项目ID", required = true)
}, result = @ResultInfo(listProperties = @ListProperty(name = "subjects", description = "科目列表", properties = {
        @Property(name = "subjectId", description = "科目ID", type = Type.String),
        @Property(name = "subjectName", description = "科目名称", type = Type.String),
        @Property(name = "libCardId", description = "绑定的题库答题卡ID", type = Type.String),
        @Property(name = "released", description = "科目成绩数据是否发布 0=未发布 1=发布成功 2=正在发布 3=发布失败", type = Type.Integer),
        @Property(name = "scoreUtime", description = "成绩更新时间", type = Type.Date)
})))
@Service
public class QueryExamSubjectStatus implements Server {

    @Autowired
    InterfaceAuthClient interfaceAuthClient;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");

        JSONArray result;
        try {
            result = interfaceAuthClient.querySubjectListByProjectId(projectId);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

        return Result.success().set("subjects", result);
    }
}
