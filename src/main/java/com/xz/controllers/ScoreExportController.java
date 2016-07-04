package com.xz.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.services.ExportScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 导出成绩到 OSS
 * created at 16/06/14
 *
 * @author yiding_he
 */
@Controller
public class ScoreExportController {

    @Autowired
    ExportScoreService exportScoreService;

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
}
