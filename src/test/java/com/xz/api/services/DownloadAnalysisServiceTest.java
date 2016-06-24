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
        Result result = downloadAnalysisService.generateZipFiles("431100-903288f61a5547f1a08a7e20420c4e9e",
                "03cd1f9c-b418-41ca-b3b8-9eb3af60ac3e", arr);
        System.out.println(result.getData());
    }
}