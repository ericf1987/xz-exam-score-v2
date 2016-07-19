package com.xz.controllers;

import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.services.ImportProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 湘潭联考：430300-672a0ed23d9148e5a2a31c8bf1e08e62
 *
 * @author yiding_he
 */
@Controller
@RequestMapping("/project")
public class ImportProjectController {

    static final Logger LOG = LoggerFactory.getLogger(ImportProjectController.class);

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    @Value("${score.upload.location}")
    private String scoreUploadLocation;

    @RequestMapping(value = "import", method = RequestMethod.POST)
    @ResponseBody
    public Result importProject(
            @RequestParam("project") String projectId,
            @RequestParam(value = "reimportStudents", required = false, defaultValue = "false") String reimportStudents
    ) {

        LOG.info("开始导入项目 " + projectId + " 基本信息...");
        boolean bReimportStudents = Boolean.valueOf(reimportStudents);
        Context context = importProjectService.importProject(projectId, bReimportStudents);

        LOG.info("项目 " + projectId + " 基本信息导入完毕。");
        return Result.success().set("questCount", context.get("questCount"));
    }

    /**
     * 从本地文件中导入网阅成绩
     *
     * @param zipFilePath 网阅成绩包路径
     * @return 执行结果
     */
    @RequestMapping(value = "import-score-pack", method = RequestMethod.POST)
    @ResponseBody
    public Result importProjectScorePack(
            @RequestParam MultipartFile zipFilePath
    ) {
        //上传原文件名
        String orginalFileName = zipFilePath.getOriginalFilename();
        //目的文件名
        String desPath = scoreUploadLocation + orginalFileName;
        if (!zipFilePath.isEmpty()) {
            //获取上传的文件的文件名
            try {
                File desFile = new File(desPath);
                zipFilePath.transferTo(desFile);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail(-1, "文件上传失败！");
            }
        } else {
            return Result.fail(-1, "文件为空，无法上传！");
        }

        //1.读取zip源文件
//        String zipPath = "F:\\chengji\\33.zip";
        try{
            ZipFileReader zipFileReader = new ZipFileReader(desPath);
            importProjectService.importStudentInfoFromZip(zipFileReader);
            return Result.success("文件上传路径为:" + desPath + "，成绩数据导入成功！");
        }catch(Exception e){
            return Result.success("数据导入失败，请重新导入！");
        }finally {
            File desFile = new File(desPath);
            if(desFile.exists()){
                try{
                    desFile.delete();
                }catch (Exception e){
                }
            }
        }

    }

}
