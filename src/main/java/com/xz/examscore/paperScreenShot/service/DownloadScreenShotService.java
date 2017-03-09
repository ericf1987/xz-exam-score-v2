package com.xz.examscore.paperScreenShot.service;

import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
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

    @Autowired
    SubjectService subjectService;

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
     * @return 结果信息
     */
    public Map<String, Object> downloadPaperScreenShot(String projectId, String schoolId, String[] classIds, String[] subjectIds) {
        if (null != subjectIds && subjectIds.length != 0) {
            return generateDownloadPath(projectId, schoolId, classIds, subjectIds);
        } else {
            List<String> sids = subjectService.querySubjects(projectId);
            return generateDownloadPath(projectId, schoolId, classIds, sids.toArray(new String[sids.size()]));
        }
    }

    /**
     * 下载班级试卷留痕zip包
     */
    public Map<String, Object> downloadGeneratedPaperScreenShot2(String projectId, String schoolId, String[] classIds) {
        String projectName = projectService.findProject(projectId).getString("name");
        String schoolName = schoolService.findSchool(projectId, schoolId).getString("name");

        //生成的压缩包路径
        File directory = new File(StringUtil.joinPaths(savePath, projectName, schoolName));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File outputFile = new File(StringUtil.joinPaths(directory.getAbsolutePath(), schoolName + "_所选班级_试卷截图" + ".zip"));

        File[] srcFiles = new File(StringUtil.joinPaths(savePath, projectName, schoolName, "所有班级")).listFiles();

        File[] requiredFiles = filterByClassName(srcFiles, projectId, classIds);

        Map<String, Object> map = doExecLinuxZipCMD(requiredFiles, outputFile);

        return map;
    }

    /**
     * 下载班级试卷留痕zip包
     */
    public Map<String, Object> downloadGeneratedPaperScreenShot(String projectId, String schoolId, String[] classIds) {
        String projectName = projectService.findProject(projectId).getString("name");
        String schoolName = schoolService.findSchool(projectId, schoolId).getString("name");

        Map<String, Object> map = new HashMap<>();

        //生成的压缩包路径
        File directory = new File(StringUtil.joinPaths(savePath, projectName, schoolName));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File outputFile = new File(StringUtil.joinPaths(directory.getAbsolutePath(), schoolName + "_所选班级_试卷截图" + ".zip"));

        ZipOutputStream out = null;
        FileInputStream fis = null;
        try {
            File[] files = new File(StringUtil.joinPaths(savePath, projectName, schoolName, "所有班级")).listFiles();

            if (null != files && files.length != 0) {

                out = new ZipOutputStream(new FileOutputStream(outputFile));

                //设置压缩级别
                out.setLevel(0);

                File[] requiredFiles = filterByClassName(files, projectId, classIds);

                for (File file : requiredFiles) {
                    fis = new FileInputStream(file);
                    ZipEntry entry = new ZipEntry(file.getName());
                    out.putNextEntry(entry);
                    int temp;
                    while ((temp = fis.read()) != -1) {
                        out.write(temp);
                    }
                    fis.close();
                }
                out.closeEntry();
                out.close();
            } else {
                map.put("downloadUrl", "");
                map.put("failPathList", "");
                map.put("failZipItemList", "");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭流
            closeStream(out, fis);
        }
        int zipSize = DownloadAnalysisService.getZipSize(outputFile.getAbsolutePath());
        map.put("downloadUrl", zipSize == 0 ? "" : StringUtil.joinPaths(downloadUrl, projectName, schoolName, outputFile.getName()));
        map.put("failPathList", "");
        map.put("failZipItemList", "");
        return map;
    }

    public void closeStream(ZipOutputStream out, FileInputStream fis) {
        try {
            if(null != fis) fis.close();
            if(null != out) out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 按班级名称过滤文件
     *
     * @param files     文件列表
     * @param projectId 项目ID
     * @param classIds  班级列表
     * @return 返回结果
     */
    private File[] filterByClassName(File[] files, String projectId, String[] classIds) {

        List<String> classNames = Arrays.asList(classIds).stream().map(c -> classService.getClassName(projectId, c)).collect(Collectors.toList());

        List<File> fileList = Arrays.asList(files).stream().filter(file -> classNames.contains(file.getName().split("_")[0])).collect(Collectors.toList());

        return fileList.toArray(new File[fileList.size()]);
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
    public Map<String, Object> generateDownloadPath(String projectId, String schoolId, String[] classIds, String[] subjectIds) {
        List<String> idPath = new LinkedList<>();
        List<String> namePath = new LinkedList<>();
        //项目名称
        String projectName = projectService.findProject(projectId).getString("name");
        //学校名称

        //创建下载文件根目录文件夹
        File directory = new File(savePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String schoolName = schoolService.findSchool(projectId, schoolId).getString("name");
        String outputFileName = StringUtil.joinPaths(savePath, projectName + "_试卷截图" + ".zip");
        for (int i = 0; i < classIds.length; i++) {
            //班级名称
            String className = classService.findClass(projectId, classIds[i]).getString("name");
            String oneSubjectId = subjectIds[i];
            String[] subjectIdArr = oneSubjectId.split("-");
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

    /**
     * 生成试卷截图压缩文件
     *
     * @param projectId  项目ID
     * @param outputFile 压缩包路径
     * @param idPath     压缩文件目录列表
     * @param namePath   试卷截图文件的生成路径列表
     * @return 返回结果
     */
    public Map<String, Object> generateDownloadZip(String projectId, File outputFile, List<String> idPath, List<String> namePath) {
        Map<String, Object> map = new HashMap<>();
        map.put("downloadUrl", outputFile.getName());
        LOG.info("试卷截图保存路径，savePath:{}", outputFile.getAbsoluteFile());
        List<String> failPathList = new ArrayList<>();
        List<String> failZipItemList = new ArrayList<>();
        ZipOutputStream out = null;
        FileInputStream fis = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(outputFile));

            for (int i = 0; i < idPath.size(); i++) {
                String currentPath = idPath.get(i);
                File[] srcFiles = new File(currentPath).listFiles();

                if (null != srcFiles && srcFiles.length != 0) {
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
                } else {
                    //LOG.info("源文件目录为{}，该目录下没有学生试卷留痕！", currentPath);
                    failPathList.add(currentPath);
                    failZipItemList.add(namePath.get(i));
                }
            }

            out.closeEntry();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            closeStream(out, fis);
        }
        int zipSize = DownloadAnalysisService.getZipSize(outputFile.getAbsolutePath());
        String outputFilePath = outputFile.getAbsolutePath();
        map.put("downloadUrl", zipSize == 0 ? "" : StringUtil.joinPaths(downloadUrl, outputFilePath.substring(savePath.length(), outputFilePath.length())));
        map.put("failPathList", failPathList);
        map.put("failZipItemList", failZipItemList);
        LOG.info("试卷截图下载路径，downloadUrl:{}", StringUtil.joinPaths(downloadUrl, outputFile.getName()));
        return map;
    }

    /**
     * 调用Linux命令压缩多个文件到指定目录
     */

    public Map<String, Object> doExecLinuxZipCMD(File[] srcFiles, File targetFile){

        File wd = new File("/bin");

        String cmd = "zip -r " + targetFile.getPath() + " " + getSrcFileZipedItems(srcFiles);
        Runtime runtime = Runtime.getRuntime();
        try {
            LOG.info("执行压缩命令为：{}", cmd);
            Process process = runtime.exec(cmd);
            LOG.info("执行压缩命令结果：{}", process.waitFor());
        } catch (Exception e) {
            LOG.info("执行命令失败：{}", e.getMessage());
        }
        Map<String, Object> map = new HashMap<>();
        int zipSize = DownloadAnalysisService.getZipSize(targetFile.getAbsolutePath());
        String targetFilePath = targetFile.getAbsolutePath();
        String url = StringUtil.joinPaths(downloadUrl, targetFilePath.substring(savePath.length(), targetFilePath.length()));
        map.put("downloadUrl", zipSize == 0 ? "" : url);
        return map;
    }

    private String getSrcFileZipedItems(File[] srcFiles) {
        StringBuilder builder = new StringBuilder();
        for(File file : srcFiles){
            builder.append(file.getPath());
            builder.append(" ");
        }
        return builder.toString();
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
