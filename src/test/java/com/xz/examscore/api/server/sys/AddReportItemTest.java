package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.services.ReportItemService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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