package com.xz.examscore.paperScreenShot.service;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import com.xz.examscore.paperScreenShot.manager.PaperScreenShotTaskManager;
import com.xz.examscore.scanner.ScannerDBService;
import com.xz.examscore.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/2/28.
 */
@Service
public class PaperScreenShotService {

    static final Logger LOG = LoggerFactory.getLogger(PaperScreenShotService.class);

    @Autowired
    SimpleCache cache;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProjectService projectService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    StudentService studentService;

    @Autowired
    ScannerDBService scannerDBService;

    @Autowired
    DispatchScreenShotTaskService dispatchScreenShotTaskService;

    @Autowired
    PaintService paintService;

    @Autowired
    PaperScreenShotTaskManager paperScreenShotTaskManager;

    public void startPaperScreenShotTask(String projectId) {
        paperScreenShotTaskManager.generatePaperScreenShots(projectId, true);
    }

    /**
     * 按班级和科目分发生成试卷截图任务
     *
     * @param projectId 项目ID
     * @param schoolId  学校ID
     * @param classId   班级ID
     * @param subjectId 科目列表
     * @return result
     */
    private PaperScreenShotsTaskByClassAndSubject runTaskByClassAndSubjectId(String projectId, String schoolId, String classId, String subjectId) {
        PaperScreenShotsTaskByClassAndSubject paperScreenShotsTaskByClassAndSubject = new PaperScreenShotsTaskByClassAndSubject(projectId, schoolId, classId, subjectId);
        paperScreenShotsTaskByClassAndSubject.start();
        return paperScreenShotsTaskByClassAndSubject;
    }

    /**
     * 封装试卷截图任务对象
     *
     * @param projectId 项目ID
     * @param schoolId  学校ID
     * @param classId   班级ID
     * @param subjectId 科目ID
     * @param taskId    任务ID
     * @return 试卷截图任务对象
     */
    public PaperScreenShotBean packScreenShotTaskBean(String projectId, String schoolId, String classId, String subjectId, String taskId) {
        List<String> studentIds = studentService.getStudentIds(projectId, Range.clazz(classId), Target.subject(subjectId));
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        for (String studentId : studentIds) {
            Map<String, Object> studentCardSlices = scannerDBService.getStudentCardSlices(projectId, subjectId, studentId);
            result.add(studentCardSlices);
        }
        return new PaperScreenShotBean(projectId, schoolId, classId, subjectId, result, taskId);
    }

    /**
     * 按班级分发任务
     *
     * @param projectId  项目ID
     * @param schoolId   学校ID
     * @param classId    班级ID
     * @param subjectIds 科目列表
     */
    public void dispatchOneClassTask(String projectId, String schoolId, String classId, List<String> subjectIds) {
        //根据科目数分发任务
        List<PaperScreenShotsTaskByClassAndSubject> tasks = subjectIds.stream().map(
                subjectId -> runTaskByClassAndSubjectId(projectId, schoolId, classId, subjectId)
        ).collect(Collectors.toList());
        for (PaperScreenShotsTaskByClassAndSubject task : tasks) {
            try {
                task.join();
                LOG.info("----生成完毕：项目{}，学校{}，班级{}，科目{}", projectId, schoolId, task.getClassId(), task.getSubjectId());
            } catch (InterruptedException e) {
                e.printStackTrace();
                LOG.info("----生成失败：项目{}，学校{}，班级{}，科目{}", projectId, schoolId, task.getClassId(), task.getSubjectId());
            }
        }
    }

    class PaperScreenShotsTaskByClassAndSubject extends Thread {
        private String projectId;
        private String schoolId;
        private String classId;
        private String subjectId;

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(String schoolId) {
            this.schoolId = schoolId;
        }

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public PaperScreenShotsTaskByClassAndSubject(String projectId, String schoolId, String classId, String subjectId) {
            this.projectId = projectId;
            this.schoolId = schoolId;
            this.classId = classId;
            this.subjectId = subjectId;
        }

        @Override
        public void run() {
            paintService.saveScreenShot(packScreenShotTaskBean(this.getProjectId(), this.getSchoolId(), this.getClassId(), subjectId, ""));
        }
    }

}
