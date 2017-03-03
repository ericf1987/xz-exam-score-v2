package com.xz.examscore.paperScreenShot.controller;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.paperScreenShot.service.PaperScreenShotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author by fengye on 2017/2/28.
 */
@Controller
@RequestMapping("/paperScreenShot")
public class PaperScreenShotController {

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @RequestMapping(value = "/start/task", method = RequestMethod.POST)
    @ResponseBody
    public Result startPaperScreenShotTask(
            @RequestParam("projectId") String projectId
    ) {
        paperScreenShotService.startPaperScreenShotTask(projectId);
        return Result.success();
    }
}