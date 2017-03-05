package com.xz.examscore.paperScreenShot.service;

import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author by fengye on 2017/3/5.
 */
@Service
public class DownloadScreenShotService {

    static final Logger LOG = LoggerFactory.getLogger(DownloadScreenShotService.class);

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProjectService projectService;

    @Value("${paper.screenshot.download.url}")
    private String downloadUrl;

    @Value("${paper.screenshot.zip.location}")
    private String savePath;

    @Value("${paper.screenshot.savepath}")
    private String srcPath;

    /**
     * 下载试卷截图压缩包
     *
     * @param projectId  项目ID
     * @param schoolId   学校ID
     * @param classIds   班级ID
     * @param subjectIds 科目列表
     * @return  结果信息
     */
    public Map<String, Object> downloadPaperScreenShot(String projectId, String schoolId, String[] classIds, List<String[]> subjectIds) {
        return generateDownloadPath(projectId, schoolId, classIds, subjectIds);
    }

    /**
     * 获取下载文件压缩包中的路径
     *
     * @param projectId  项目ID
     * @param schoolId   学校ID
     * @param classIds   班级列表
     * @param subjectIds 科目列表
     * @return 结果信息
     */
    public Map<String, Object> generateDownloadPath(String projectId, String schoolId, String[] classIds, List<String[]> subjectIds){
        List<String> idPath = new LinkedList<>();
        List<String> namePath = new LinkedList<>();
        //项目名称
        String projectName = projectService.findProject(projectId).getString("name");
        //学校名称
        String schoolName = schoolService.findSchool(projectId, schoolId).getString("name");
        String outputFileName = StringUtil.joinPaths(downloadUrl, projectName, ".zip");
        for (int i = 0; i < classIds.length; i++) {
            //班级名称
            String className = classService.findClass(projectId, classIds[i]).getString("name");
            String[] subjectIdArr = subjectIds.get(i);
            for (String subjectId : subjectIdArr) {
                String subjectName = SubjectService.getSubjectName(subjectId);
                //已生成的文件路径
                String path = StringUtil.joinPaths(srcPath, projectId, schoolId, classIds[i], subjectId);
                String dir = StringUtil.joinPaths(schoolName, className, subjectName);
                //下载的压缩包文件路径
                idPath.add(path);
                namePath.add(dir);
            }
        }
        return generateDownloadZip(projectId, new File(outputFileName), idPath, namePath);
    }

    public Map<String, Object> generateDownloadZip(String projectId, File outputFile, List<String> idPath, List<String> namePath) {
        Map<String, Object> map = new HashMap<>();
        map.put("downloadUrl", outputFile.getName());
        List<String> failPathList = new ArrayList<>();
        List<String> failZipItemList = new ArrayList<>();
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
            FileInputStream fis;

            for (int i = 0; i < idPath.size(); i++) {
                String currentPath = idPath.get(i);
                File[] srcFiles = new File(currentPath).listFiles();
                if(null != srcFiles && srcFiles.length == 0){
                    LOG.info("源文件目录为{}，该目录下没有学生试卷留痕！", currentPath);
                    failPathList.add(currentPath);
                    failZipItemList.add(namePath.get(i));
                }
                for (File file : srcFiles) {
                    fis = new FileInputStream(file);
                    ZipEntry entry = new ZipEntry(namePath.get(i) + "/" + convertFileName(projectId, file.getName()));
                    out.putNextEntry(entry);
                    int temp;
                    while ((temp = fis.read()) != -1) {
                        out.write(temp);
                    }
                    fis.close();
                }
            }
            out.closeEntry();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("failPathList", failPathList);
        map.put("failZipItemList", failZipItemList);
        return map;
    }

    private String convertFileName(String projectId, String name) {
        // TODO: 2017/3/5 如何处理多个学生名字相同的情况，此方法需完善
        String[] s = name.split("_");
        Document student = studentService.findStudent(projectId, s[0]);
        String studentName = student.getString("name");
        String examNo = StringUtil.isBlank(student.getString("examNo")) ? String.valueOf(Math.random()) : student.getString("examNo");
        String tag = s[1].contains("positive") ? s[1].replace("positive", "正面") : s[1].replace("reverse", "反面");
        return studentName + "_" + examNo + "_" + String.valueOf(Math.round(Math.random() * 100)) + "_" + tag;
    }
}
