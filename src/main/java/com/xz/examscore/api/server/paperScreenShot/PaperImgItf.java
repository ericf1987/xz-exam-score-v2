package com.xz.examscore.api.server.paperScreenShot;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.paperScreenShot.service.PaperImgService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2017/5/20.
 */
@Function(description = "试卷截图数据接口", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = false),
        @Parameter(name = "subjectId", type = Type.String, description = "科目id", required = false),
        @Parameter(name = "studentId", type = Type.String, description = "学生id", required = false),
        @Parameter(name = "isPositive", type = Type.String, description = "正面或反面", required = false)
})
@Service
public class PaperImgItf implements Server{

    @Autowired
    PaperImgService paperImgService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String classId = param.getString("classId");
        String subjectId = param.getString("subjectId");
        String studentId = param.getString("studentId");
        boolean isPositive = BooleanUtils.toBoolean(param.getString("isPositive"));
        String imgString = paperImgService.getOneStuOnePage(projectId, schoolId, classId, subjectId, studentId, isPositive, null);
        return Result.success().set("imgString", imgString);
    }
}
