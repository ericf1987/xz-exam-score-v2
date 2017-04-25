package com.xz.examscore.paperScreenShot.service;

import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.services.ProjectService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author by fengye on 2017/4/24.
 */
@Service
public class PickUpStudentsScreenShots {

    @Value("${paper.screenshot.savepath}")
    private String paperScreenShotSavePath;

    @Value("${paper.screenshot.zip.location}")
    private String paperScreenZipLocation;

    @Value("${paper.screenshot.download.url}")
    private String downloadUrl;

    @Autowired
    PaintService paintService;

    @Autowired
    ProjectService projectService;

    @Autowired
    DownloadScreenShotService downloadScreenShotService;

    //下载指定学生的试卷截图
    public List<String> downLoadScreenShotByStudents(String projectId, List<Document> students, String subjectId) {
        List<String> paths = new LinkedList<>();
        List<String> names = new LinkedList<>();
        String projectName = projectService.findProject(projectId).getString("name");
        for (Document student : students) {
            String schoolId = student.getString("school");
            String classId = student.getString("class");
            String studentId = student.getString("student");
            String name = student.getString("name");
            String examNo = student.getString("examNo");
            String positive = StringUtil.joinPaths(
                    paperScreenShotSavePath,
                    projectId, schoolId, classId, subjectId, studentId, "_positive.png"
                    );
            String reverse = StringUtil.joinPaths(
                    paperScreenShotSavePath,
                    projectId, schoolId, classId, subjectId, studentId, "_reverse.png"
                    );
            paths.add(positive);
            paths.add(reverse);

            names.add(name + examNo);
            names.add(name + examNo);
        }

        String outputFileName = projectName + "-指定学生试卷截图包.zip";

        downloadScreenShotService.generateDownloadZip(projectId, new File(outputFileName), paths, names);

        return paths;
    }


}
