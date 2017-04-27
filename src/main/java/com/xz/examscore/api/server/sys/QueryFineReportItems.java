package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.FineReportService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 帆软个性化报表数据接口
 * @author by fengye on 2017/4/25.
 */
@Service
public class QueryFineReportItems implements Server{

    @Autowired
    FineReportService fineReportService;

    @Override
    public Result execute(Param param) throws Exception {
        List<Document> allItems = fineReportService.getAllItems();
        return Result.success().set("allItems", allItems);
    }
}
