package com.xz.examscore.api.server.download;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.DownloadAnalysisService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2016/6/14.
 */
@Function(description = "总体成绩-报表下载", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false),
        @Parameter(name = "fileParam", type = Type.StringArray, description = "文件参数", required = false),
        @Parameter(name = "isBureau", type = Type.String, description = "是否是教育局账号", required = true)
})
@Service
public class DownLoadAnalysis implements Server{

    @Autowired
    DownloadAnalysisService downloadAnalysisService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String[] fileParam = param.getStringValues("fileParam");
        boolean isBureau = BooleanUtils.toBoolean(Boolean.valueOf(param.getString("isBureau")));
        return downloadAnalysisService.generateZipFile(projectId, schoolId, fileParam, isBureau);
    }
}
