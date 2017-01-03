package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/1/2.
 */
public class ExamAllianceReportService {
    @Value("${zip.download.url}")
    private String downloadURL;

    @Value("${report.generator.savepath}")
    private String savePath;

    @Value("${report.zip.location}")
    private String downloadPath;

    @Autowired
    ProjectService projectService;

    @Autowired
    DownloadAnalysisService downLoadAnalysisService;

    public static final String[] EXCEL_FILE_NAME = new String[]{
            "前百分段名平均分.xlsx",
            "800分以上人数统计.xlsx",
            "本科及上线率分析.xlsx",
            "客观题突出情况.xlsx",
            "试题得分明细.xlsx",
            "主观题突出情况.xlsx",
            "单科各校基本情况.xlsx",
            "各科基本情况.xlsx",
            "一本入围比例核算.xlsx",
            "临界生人数及各科得分率.xlsx",
            "整体平均分统计.xlsx",
            "试卷难度系数.xlsx",
            "分数段表.xlsx"
    };

    public Result generateReports(String projectId, boolean b) {
        return Result.success();
    }

    public Result downloadReports(String projectId){
        String projectName = projectService.findProject(projectId).getString("name");
        String zipFileName = projectName + "-联考分析报表.zip";
        List<Map<String, String>> pathList = new ArrayList<>();

        for (String fileName : EXCEL_FILE_NAME) {
            List<Map<String, String>> category = getFileCategory(projectId, fileName);
            pathList.addAll(category);
        }

        Map<String, Object> resultMap = createZipFiles(projectId, pathList, zipFileName);
        return Result.success().set("downloadInfo", resultMap);
    }

    private List<Map<String, String>> getFileCategory(String projectId, String fileName) {
        return null;
    }

    private Map<String,Object> createZipFiles(String projectId, List<Map<String, String>> pathList, String zipFileName) {
        //压缩文件的生成目录
        File dir = new File(downloadPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //下载zip包的生成路径
        String directory = downloadPath + File.separator +zipFileName;
        File file = new File(directory);
        Map<String, Object> resultMap = new HashMap<>();
        List<String> failureList = new ArrayList<>();
        downLoadAnalysisService.doZipOperation(pathList, file, failureList);
        //判断压缩文件中是否有文件条目
        int size = downLoadAnalysisService.getZipSize(directory);
        if (size != 0) {
            //zip文件下载url
            resultMap.put("downloadURL", downloadURL + File.separator +zipFileName);
        } else {
            resultMap.put("downloadURL", "");
        }
        //不存在的文件列表
        resultMap.put("failureList", failureList);
        return resultMap;
    }

}
