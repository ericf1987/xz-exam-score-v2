package com.xz.examscore.controllers;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.server.customization.StudentEvaluationFormAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author by fengye on 2017/5/11.
 */
@Controller
@RequestMapping("/test")
public class TestController {

    static final Logger LOG = LoggerFactory.getLogger(ZipScoreUploadController.class);

    @Autowired
    StudentEvaluationFormAnalysis studentEvaluationFormAnalysis;

    @RequestMapping(value = "/student_evaluation", method = RequestMethod.POST)
    @ResponseBody
    public Result execute(@RequestParam("project") String projectId) {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = null;
        try {
            result = studentEvaluationFormAnalysis.execute(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
