package com.xz.api.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.services.DownloadAnalysisService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/14.
 */
public class DownloadAnalysisServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    DownloadAnalysisService downloadAnalysisService;

    @Test
    public void testMain() throws Exception {
        String[] arr = new String[]{
                "班级成绩分析-基础分析-排名统计",
                "学校成绩分析-基础分析-排名统计",
                "总体成绩分析-基础分析-排名统计"
        };
        Result result = downloadAnalysisService.generateZipFiles("430300-672a0ed23d9148e5a2a31c8bf1e08e62",
                "11b66fc2-8a76-41c2-a1b3-5011523c7e47", arr);
        System.out.println(result.getData());
    }
}