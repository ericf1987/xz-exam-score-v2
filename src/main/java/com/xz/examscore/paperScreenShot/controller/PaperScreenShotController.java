package com.xz.examscore.paperScreenShot.controller;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.paperScreenShot.service.DownloadScreenShotService;
import com.xz.examscore.paperScreenShot.service.PaperScreenShotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/start/task", method = RequestMethod.POST)
    @ResponseBody
    public Result startPaperScreenShotTask(
            @RequestParam("projectId") String projectId
    ) {
        paperScreenShotService.startPaperScreenShotTask(projectId);
        return Result.success();
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    @ResponseBody
    public Result downloadPaperScreenShot(
            @RequestParam("projectId") String projectId,
            @RequestParam("schoolId") String schoolId,
            @RequestParam("classIds") String[] classIds,
            @RequestParam("subjectIds") List<String[]> subjectIds
            ){
        Map<String, Object> downloadInfo = downloadScreenShotService.downloadPaperScreenShot(projectId, schoolId, classIds, subjectIds);
        return Result.success().set("downloadInfo", downloadInfo);
    }
}