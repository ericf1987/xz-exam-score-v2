package com.xz.examscore.paperScreenShot.controller;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.bean.PaperScreenShotStatus;
import com.xz.examscore.paperScreenShot.service.DownloadScreenShotService;
import com.xz.examscore.paperScreenShot.service.PaintService;
import com.xz.examscore.paperScreenShot.service.PaperScreenShotService;
import com.xz.examscore.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author by fengye on 2017/2/28.
 */
@Controller
@RequestMapping("/paperScreenShot")
public class PaperScreenShotController {

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Autowired
    DownloadScreenShotService downloadScreenShotService;

    @Autowired
    PaintService paintService;

    @Autowired
    ProjectService projectService;

    @RequestMapping(value = "/start/task", method = RequestMethod.POST)
    @ResponseBody
    public Result startPaperScreenShotTask(
            @RequestParam("projectId") String projectId
    ) {
        if (projectService.getPaperScreenShotStatus(projectId).equals(PaperScreenShotStatus.GENERATING)) {
            return Result.fail("该考试项目正在保存截图，请等待...");
        }
        return paperScreenShotService.startPaperScreenShotTask(projectId);
    }

    @RequestMapping(value = "/start/task/oneStudent", method = RequestMethod.POST)
    @ResponseBody
    public Result startPaperScreenShotTask(
            @RequestParam("projectId") String projectId,
            @RequestParam("studentId") String studentId,
            @RequestParam("subjectId") String subjectId,
            @RequestParam(value = "generateClassZip", required = false, defaultValue = "false") String generateClassZip
    ) {
        return paperScreenShotService.generateOneStuPaperScreenShot(projectId, studentId, subjectId, Boolean.valueOf(generateClassZip));
    }

    @RequestMapping(value = "/downloadByClass", method = RequestMethod.POST)
    @ResponseBody
    public Result downloadPaperScreenShot(
            @RequestParam("projectId") String projectId
            ){

        if (projectService.getPaperScreenShotStatus(projectId).equals(PaperScreenShotStatus.GENERATING)) {
            return Result.fail("该考试项目正在保存截图，请等待...");
        }

        paperScreenShotService.generateClassPaperScreenShotZip(projectId);

        return Result.success("保存截图任务开始执行...");
    }

    @RequestMapping(value = "/fonts", method = RequestMethod.POST)
    @ResponseBody
    public Result getAvailableFontFamilyNames(){
        List<String> availableFontFamilyNames = paintService.getAvailableFontFamilyNames();
        return Result.success().set("fonts", availableFontFamilyNames);
    }
}