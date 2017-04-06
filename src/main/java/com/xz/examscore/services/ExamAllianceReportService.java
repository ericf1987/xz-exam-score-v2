package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/1/2.
 */
@Service
public class ExamAllianceReportService {
    @Value("${examAlliance.zip.download.url}")
    private String downloadURL;

    @Value("${examAlliance.report.generator.savepath}")
    private String savePath;

    @Value("${examAlliance.report.zip.location}")
    private String downloadPath;

    @Autowired
    ProjectService projectService;

    @Autowired
    DownloadAnalysisService downLoadAnalysisService;

    public static final String[] EXCEL_FILE_NAME = new String[]{
            "联考成绩分析-->基础分析-->前百分段名平均分.xlsx",
            "联考成绩分析-->基础分析-->800分以上人数统计.xlsx",
            "联考成绩分析-->基础分析-->本科及上线率分析.xlsx",
            "联考成绩分析-->基础分析-->客观题突出情况.xlsx",
            "联考成绩分析-->基础分析-->试题得分明细.xlsx",
            "联考成绩分析-->基础分析-->主观题突出情况.xlsx",
            "联考成绩分析-->基础分析-->单科各校基本情况.xlsx",
            "联考成绩分析-->基础分析-->各科基本情况.xlsx",
            "联考成绩分析-->基础分析-->一本入围比例核算.xlsx",
            "联考成绩分析-->基础分析-->临界生人数及各科得分率.xlsx",
            "联考成绩分析-->基础分析-->整体平均分统计.xlsx",
            "联考成绩分析-->基础分析-->试卷难度系数.xlsx",
            "联考成绩分析-->基础分析-->分数段表.xlsx",
            "联考成绩分析-->基础分析-->题目能力层级分析.xlsx",
            "联考成绩分析-->基础分析-->学校分数分段统计（10分段）.xlsx",
            "联考成绩分析-->基础分析-->学校分数段累积统计（10分段）.xlsx"
    };

    public Result downloadReports(String projectId) {
        String projectName = projectService.findProject(projectId).getString("name");
        String zipFileName = projectName + "-联考分析报表.zip";
        List<Map<String, String>> pathList = new ArrayList<>();

        for (String fileName : EXCEL_FILE_NAME) {
            String[] param = fileName.split("-->");
            List<Map<String, String>> category = downLoadAnalysisService.getFileCategory(projectId, fileName, param, false);
            pathList.addAll(category);
        }

        Map<String, Object> resultMap = createZipFiles(pathList, zipFileName);
        return Result.success().set("downloadInfo", resultMap);
    }

    private Map<String, Object> createZipFiles(List<Map<String, String>> pathList, String zipFileName) {
        //压缩文件的生成目录
        File dir = new File(downloadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //下载zip包的生成路径
        String directory = downloadPath + zipFileName;
        File file = new File(directory);
        Map<String, Object> resultMap = new HashMap<>();
        List<String> failureList = new ArrayList<>();
        downLoadAnalysisService.doZipOperation(pathList, file, failureList);
        //判断压缩文件中是否有文件条目
        int size = downLoadAnalysisService.getZipSize(directory);
        if (size != 0) {
            //zip文件下载url
            resultMap.put("downloadURL", downloadURL + zipFileName);
        } else {
            resultMap.put("downloadURL", "");
        }
        //不存在的文件列表
        resultMap.put("failureList", failureList);
        return resultMap;
    }


}
