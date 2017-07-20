package com.xz.examscore.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.importaggrdata.service.ImportFromMysqlDump;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author by fengye on 2017/7/19.
 */
@Controller
@RequestMapping("/import-aggr-data")
public class ImportFromMysqlDumpController {

    @Autowired
    ImportFromMysqlDump importFromMysqlDump;

    @RequestMapping(value = "/importMysqlDump", method = RequestMethod.GET)
    @ResponseBody
    public Result importFromMysqlDump(
            @RequestParam(value="projectId", defaultValue = "") String projectId,
            @RequestParam(value="filePath", defaultValue = "") String filePath
    ) {
        return importFromMysqlDump.importData(projectId, filePath);
    }

}
