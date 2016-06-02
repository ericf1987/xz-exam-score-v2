package com.xz.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.intclient.InterfaceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
@Controller
@RequestMapping("/project")
public class ImportProjectController {

    @Autowired
    InterfaceClient interfaceClient;

    @RequestMapping(value = "import", method = RequestMethod.POST)
    public Result importProject(
            @RequestParam("project") String projectId) {

        importProjectInfo(projectId);
        importSubjects(projectId);
        importQuests(projectId);
        importSchools(projectId);
        importStudents(projectId);

        return Result.success();
    }

    private void importStudents(String projectId) {

    }

    private void importSchools(String projectId) {

    }

    private void importQuests(String projectId) {

    }

    private void importSubjects(String projectId) {

    }

    private void importProjectInfo(String projectId) {

    }
}
