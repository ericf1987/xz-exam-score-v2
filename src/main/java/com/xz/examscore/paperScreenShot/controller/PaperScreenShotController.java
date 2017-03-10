package com.xz.examscore.paperScreenShot.controller;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.paperScreenShot.service.DownloadScreenShotService;
import com.xz.examscore.paperScreenShot.service.PaintService;
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

    @Autowired
    PaintService paintService;

    @RequestMapping(value = "/start/task", method = RequestMethod.POST)
    @ResponseBody
    public Result startPaperScreenShotTask(
            @RequestParam("projectId") String projectId
    ) {
        return paperScreenShotService.startPaperScreenShotTask(projectId);
    }

    @RequestMapping(value = "/downloadByClass", method = RequestMethod.POST)
    @ResponseBody
    public Result downloadPaperScreenShot(
            @RequestParam("projectId") String projectId,
            @RequestParam("schoolId") String schoolId
            ){
        List<Map<String, Object>> downloadInfo = paperScreenShotService.generateClassPaperScreenShot(projectId, schoolId);
        return Result.success().set("downloadInfo", downloadInfo);
    }

    @RequestMapping(value = "/fonts", method = RequestMethod.POST)
    @ResponseBody
    public Result getAvailableFontFamilyNames(){
        List<String> availableFontFamilyNames = paintService.getAvailableFontFamilyNames();
        return Result.success().set("fonts", availableFontFamilyNames);
    }
}