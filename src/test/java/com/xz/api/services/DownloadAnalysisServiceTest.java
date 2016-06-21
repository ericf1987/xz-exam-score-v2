package com.xz.api.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.services.ClassService;
import com.xz.services.DownloadAnalysisService;
import com.xz.services.SchoolService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/14.
 */
public class DownloadAnalysisServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    DownloadAnalysisService downloadAnalysisService;

    @Test
    public void testMain() throws Exception {
        String[] arr = new String[]{
            "100-200-300","100-200-301","100-200-302",
            "100-201-300","100-201-301","100-201-302",
            "101-200-300","101-200-301","101-200-302",
            "101-201-300","101-201-301","101-201-302",
            "102-200-300","102-200-301","102-200-302",
            "102-201-300","102-201-301","102-201-302",
        };
        Result result = downloadAnalysisService.generateZipFiles("430300-672a0ed23d9148e5a2a31c8bf1e08e62",
                "11b66fc2-8a76-41c2-a1b3-5011523c7e47", arr);
        System.out.println(result.getData());
    }
}