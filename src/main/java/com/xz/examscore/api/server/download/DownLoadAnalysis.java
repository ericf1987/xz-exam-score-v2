package com.xz.examscore.api.server.download;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.DownloadAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2016/6/14.
 */
@Function(description = "总体成绩-报表下载", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "fileParam", type = Type.StringArray, description = "文件参数", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = false)
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
        return downloadAnalysisService.generateZipFiles(projectId, schoolId, fileParam);
    }
}
