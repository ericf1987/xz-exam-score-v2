package com.xz.controllers;

import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.services.ImportProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * @param filePath 网阅成绩包路径
     * @return 执行结果
     */
    @RequestMapping(value = "import-score-pack", method = RequestMethod.POST)
    @ResponseBody
    public Result importProjectScorePack(
            @RequestParam(value = "filePath", required = true) String filePath
    ) {
        LOG.debug("服务器文件路径-->{}" + filePath);

        //1.读取zip源文件
//        String zipPath = "F:\\chengji\\33.zip";
        ZipFileReader zipFileReader = new ZipFileReader(filePath);
        importProjectService.importStudentInfoFromZip(zipFileReader);
        return Result.success();
    }

}
