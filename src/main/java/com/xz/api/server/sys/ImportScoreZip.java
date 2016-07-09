package com.xz.api.server.sys;

import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.services.ImportProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2016/7/8.
 */
@Function(description = "学生成绩zip包导入", parameters = {
        @Parameter(name = "filePath", type = Type.String, description = "文件路径", required = true)
})
@Service
public class ImportScoreZip implements Server{
    static final Logger LOG = LoggerFactory.getLogger(ImportScoreZip.class);

    @Autowired
    ImportProjectService importProjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String filePath = param.getString("filePath");
        LOG.debug("待导入的文件路径-->{}" + filePath);
        ZipFileReader zipFileReader = new ZipFileReader(filePath);
        importProjectService.importStudentInfoFromZip(zipFileReader);
        return Result.success().set("desc", "文件上传路径为" + filePath + "，成绩数据导入成功！");
    }
}
