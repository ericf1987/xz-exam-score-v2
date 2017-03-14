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

    public static final long SCREENSHOT_ZIP_FILE_VALIDITY = 1000 * 60 * 60;

    /**
     * 下载班级试卷留痕zip包
     */
    public Map<String, Object> downloadGeneratedPaperScreenShot(String projectId, String schoolId, String[] classIds) {
        String projectName = projectService.findProject(projectId).getString("name");
        String schoolName = schoolService.findSchool(projectId, schoolId).getString("name");

        //生成的压缩包路径
        File directory = new File(StringUtil.joinPaths(savePath, projectName, schoolName));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //清理时间超过一个小时的文件包
        clearScreenShotZips(directory);

        //时间戳
        long currentTimeMillis = System.currentTimeMillis();

        File outputFile = new File(StringUtil.joinPaths(directory.getAbsolutePath(), schoolName + "_所选班级_试卷截图_" + String.valueOf(currentTimeMillis) + ".zip"));

        File[] srcFiles = new File(StringUtil.joinPaths(savePath, projectName, schoolName, "所有班级")).listFiles();

        File[] requiredFiles = filterByClassName(srcFiles, projectId, classIds);

        return doExecLinuxZipCMD(requiredFiles, outputFile);
    }

    private void clearScreenShotZips(File directory) {
        try {
            long now = System.currentTimeMillis();
            for(File file : directory.listFiles()){
                if(file.isFile()){
                    String[] slice = file.getName().split("\\.")[0].split("_");
                    String timeStamp = slice[slice.length - 1];

                    LOG.info("当前文件为：{}, 文件的创建时间为：{}", file.getName(), timeStamp);
                    if(Long.valueOf(timeStamp) < now - SCREENSHOT_ZIP_FILE_VALIDITY){
                        boolean success = file.delete();
                        LOG.info("清理截图文件, 文件名：{}, 操作结果：{}", file.getAbsoluteFile(), success ? "成功" : "失败");
                    }
                }
            }
        } catch (NumberFormatException e) {
            LOG.error("清理截图文件包出现异常！");
        }
    }

    public void closeStream(ZipOutputStream out, FileInputStream fis) {
        try {
            if(null != fis) fis.close();
            if(null != out) out.close();
        } catch (IOException e) {
            e.printStackTrace();
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

        //不压缩子目录
        String cmd = "zip -j " + targetFile.getPath() + " " + getSrcFileZippedItems(srcFiles);
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

    private String getSrcFileZippedItems(File[] srcFiles) {
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
