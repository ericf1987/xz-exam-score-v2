package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.ImportProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author by fengye on 2016/7/8.
 */
@Function(description = "学生成绩zip包导入", parameters = {
        @Parameter(name = "filePath", type = Type.String, description = "文件路径", required = true)
})
@Service
public class ImportScoreZip implements Server{

    @Autowired
    ImportProjectService importProjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String filePath = param.getString("filePath");
        ZipFileReader zipFileReader = new ZipFileReader(filePath);
        try {
            importProjectService.importStudentInfoFromZip(zipFileReader);
            return Result.success("文件上传路径为" + filePath + "，成绩数据导入成功！");
        } catch (Exception e) {
            return Result.fail("数据导入出现异常，请重新操作!");
        }finally {
            File desFile = new File(filePath);
            if(desFile.exists()){
                try{
                    desFile.delete();
                }catch (Exception e){
                }
            }
        }
    }
}
