package com.xz.examscore.paperScreenShot.service;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.bean.PaperScreenShotStatus;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import com.xz.examscore.paperScreenShot.bean.TaskProcess;
import com.xz.examscore.paperScreenShot.manager.AllClassScreenShotZipGenerator;
import com.xz.examscore.paperScreenShot.manager.PaperScreenShotTaskManager;
import com.xz.examscore.scanner.ScannerDBService;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/2/28.
 */
@Service
public class PaperScreenShotService {

    static final Logger LOG = LoggerFactory.getLogger(PaperScreenShotService.class);

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

    @Autowired
    AllClassScreenShotZipGenerator allClassScreenShotZipGenerator;

    @Autowired
    DownloadScreenShotService downloadScreenShotService;

    @Autowired
    MonitorService monitorService;

    @Value("${paper.screenshot.zip.location}")
    private String savePath;

    @Value("${paper.screenshot.savepath}")
    private String srcPath;

    /**
     * 试卷截图保存任务执行入口
     *
     * @param projectId 项目ID
     * @return 执行结果
     */
    public Result startPaperScreenShotTask(String projectId) {

        LOG.info("----项目{}，开始执行试卷截图保存任务----");

        LOG.info("====项目{}， 开始保存截图====");
        projectService.setPaperScreenShotStatus(projectId, PaperScreenShotStatus.GENERATING);
        paperScreenShotTaskManager.generatePaperScreenShots(projectId, true);
        LOG.info("====项目{}， 保存截图完成====");

        LOG.info("====项目{}， 开始打包班级试卷截图压缩包====");
        allClassScreenShotZipGenerator.generateClassPaperScreenShot(projectId, true);
        LOG.info("====项目{}， 班级试卷截图压缩包生成完毕====");

        LOG.info("----项目{}，试卷截图任务执行完成----");

        projectService.setPaperScreenShotStatus(projectId, PaperScreenShotStatus.GENERATED);

        return Result.success("试卷截图保存任务执行完成！");
    }

    /**
     * 重新生成单个学生的试卷截图
     *
     * @param projectId        项目ID
     * @param studentId        学生ID
     * @param subjectId        科目ID
     * @param generateClassZip 是否创建班级试卷截图压缩包
     * @return 返回结果
     */
    public Result generateOneStuPaperScreenShot(String projectId, String studentId, String subjectId, Boolean generateClassZip) {
        Document student = studentService.findStudent(projectId, studentId);
        String schoolId = student.getString("school");
        String classId = student.getString("class");
        PaperScreenShotBean paperScreenShotBean = packOneStuScreenShotTaskBean(projectId, schoolId, classId, subjectId, studentId, "");

        LOG.info("====项目{}， 重新生成学生{}，科目{}的试卷截图====");
        paintService.saveScreenShot(paperScreenShotBean, null);
        LOG.info("====项目{}， 学生{}，科目{}的试卷截图生成完毕====");

        if (generateClassZip) {
            LOG.info("====项目{}， 重新打包班级{}的试卷截图压缩包====");
            generateOneClassZip(projectId, schoolId, classId, Collections.singletonList(subjectId));
            LOG.info("====项目{}， 班级{}的试卷截图压缩包生成完毕====");
        }

        return Result.success("执行完成！");

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
     * 单个学生的试卷截图任务
     *
     * @param projectId 项目ID
     * @param schoolId  学校ID
     * @param classId   班级ID
     * @param subjectId 科目ID
     * @param studentId 学生ID
     * @param taskId    任务ID
     * @return 试卷截图任务对象
     */
    public PaperScreenShotBean packOneStuScreenShotTaskBean(String projectId, String schoolId, String classId, String subjectId, String studentId, String taskId) {
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        Map<String, Object> studentCardSlices = scannerDBService.getStudentCardSlices(projectId, subjectId, studentId);

        result.add(studentCardSlices);

        return new PaperScreenShotBean(projectId, schoolId, classId, subjectId, result, taskId);
    }

    /**
     * 按班级和科目分发任务
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
            } catch (InterruptedException e) {
                e.printStackTrace();
                LOG.info("----生成失败：项目{}，学校{}，班级{}，科目{}", projectId, schoolId, task.getClassId(), task.getSubjectId());
            }
        }
        monitorService.increaseFinished(projectId, TaskProcess.GENERATE_PAPER_SCREEN_SHOT);
    }

    /**
     * 将考试项目下所有班级的试卷留痕文件打包保存
     *
     * @param projectId 项目ID
     * @return 返回结果
     */
    public Result generateClassPaperScreenShotZip(String projectId) {
        allClassScreenShotZipGenerator.generateClassPaperScreenShot(projectId, true);
        return Result.success();
    }

    /**
     * 生成单个班级的试卷截图压缩包
     *
     * @param projectId 项目ID
     * @param schoolId  学校ID
     * @param classId   班级ID
     * @param subjects  科目列表
     */
    public void generateOneClassZip(String projectId, String schoolId, String classId, List<String> subjects) {
        String projectName = projectService.findProject(projectId).getString("name");
        String schoolName = schoolService.findSchool(projectId, schoolId).getString("name");

        //保存路径
        File directory = new File(StringUtil.joinPaths(savePath, projectName, schoolName, "所有班级"));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String className = classService.getClassName(projectId, classId);
        //班级留痕文件的文件名
        String outputFileName = StringUtil.joinPaths(directory.getAbsolutePath(), className + "_试卷截图" + ".zip");
        List<String> idPath = new LinkedList<>();
        List<String> namePath = new LinkedList<>();
        for (String subjectId : subjects) {
            String subjectName = SubjectService.getSubjectName(subjectId);
            //已生成的文件路径
            String path = StringUtil.joinPaths(srcPath, projectId, schoolId, classId, subjectId);
            String dir = StringUtil.joinPaths(className, subjectName);
            //下载的压缩包文件路径
            idPath.add(path);
            namePath.add(dir);
        }
        downloadScreenShotService.generateDownloadZip(projectId, new File(outputFileName), idPath, namePath);
        monitorService.increaseFinished(projectId, TaskProcess.GENERATE_CLASS_ZIP);
    }

    class PaperScreenShotsTaskByClassAndSubject extends Thread {
        private String projectId;
        private String schoolId;
        private String classId;
        private String subjectId;

        //添加一个题目过滤规则
        private Map<String, Object> rankRuleMap = new HashMap<>();

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

        public Map<String, Object> getRankRuleMap() {
            return rankRuleMap;
        }

        public void setRankRuleMap(Map<String, Object> rankRuleMap) {
            this.rankRuleMap = rankRuleMap;
        }

        public PaperScreenShotsTaskByClassAndSubject(String projectId, String schoolId, String classId, String subjectId) {
            this.projectId = projectId;
            this.schoolId = schoolId;
            this.classId = classId;
            this.subjectId = subjectId;
        }

        public PaperScreenShotsTaskByClassAndSubject(String projectId, String schoolId, String classId, String subjectId, Map<String, Object> rankRuleMap) {
            this.projectId = projectId;
            this.schoolId = schoolId;
            this.classId = classId;
            this.subjectId = subjectId;
            this.rankRuleMap = rankRuleMap;
        }

        @Override
        public void run() {
            paintService.saveScreenShot(packScreenShotTaskBean(this.getProjectId(), this.getSchoolId(), this.getClassId(), this.getSubjectId(), ""), null);
        }
    }

}
