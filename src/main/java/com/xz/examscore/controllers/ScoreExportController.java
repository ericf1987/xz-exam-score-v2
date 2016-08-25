package com.xz.examscore.controllers;

import com.xz.ajiaedu.common.aliyun.OSSFileClient;
import com.xz.ajiaedu.common.io.FileUtils;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.services.ExportScoreService;
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
 * 导出成绩到 OSS
 * created at 16/06/14
 *
 * @author yiding_he
 */
@Controller
public class ScoreExportController {

    @Value("${zip.save.location}")
    private String zipSaveLocation;     // zip 保存位置

    @Value("${oss.url.prefix}")
    private String ossUrlPrefix;        // zip 保存位置

    @Autowired
    ExportScoreService exportScoreService;

    @Autowired
    OSSFileClient componentUpdateOssFileClient;

    /**
     * 导出成绩到阿里云
     *
     * @param projectId       项目ID
     * @param notifyInterface 是否要通知接口导入成绩
     *
     * @return 操作结果
     */
    @RequestMapping(value = "export-score-to-oss", method = RequestMethod.POST)
    @ResponseBody
    public Result exportScore(
            @RequestParam("project") String projectId,
            @RequestParam(value = "notifyInterface", required = false, defaultValue = "false") boolean notifyInterface
    ) {
        try {
            String ossPath = exportScoreService.exportScore(projectId, notifyInterface);
            return Result.success().set("ossPath", ossPath);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 直接上传文件到 OSS （用于组件更新）
     *
     * @param file      要上传的文件
     * @param component 组件名
     * @param version   版本
     *
     * @return 上传结果
     */
    @RequestMapping(value = "upload-file-to-oss", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadExamZip(
            @RequestParam MultipartFile file,
            @RequestParam("component") String component,
            @RequestParam("version") String version
    ) throws Exception {

        if (StringUtil.isEmpty(component) || StringUtil.isEmpty(version) || file.isEmpty()) {
            return Result.fail("参数不能为空");
        }

        String saveFilePath = zipSaveLocation + "oss-upload/" + component + "/" + version + "/update.zip";
        String ossFilePath = "updates/" + component + "/" + version + "/update.zip";

        file.transferTo(FileUtils.getOrCreateFile(saveFilePath));
        componentUpdateOssFileClient.uploadFile(new File(saveFilePath), ossFilePath);

        return Result.success().set("url", ossUrlPrefix + ossFilePath);
    }

}
