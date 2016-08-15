package com.xz.api.server.sys;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.services.ReportItemService;
import com.xz.services.ReportService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/8/11.
 */
public class AddReportItemTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AddReportItem addReportItem;

    @Test
    public void testExecute() throws Exception {

        String[] collectionNames = new String[]{
                //"score", "total_score"
                "rank_level_map"
        };

        Param param = new Param()
                .setParameter("name", "等第分析")
                .setParameter("type", "basics")
                .setParameter("rangeName", "clazz")
                .setParameter("rangeId", ReportItemService.COMMON_RANGE_ID)
                .setParameter("collectionNames", collectionNames)
                .setParameter("serverName", "ClassRankLevelAnalysis");
        Result result = addReportItem.execute(param);
        System.out.println(result.getData());
    }
}