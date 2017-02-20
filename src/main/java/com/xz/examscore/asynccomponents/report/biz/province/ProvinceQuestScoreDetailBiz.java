package com.xz.examscore.asynccomponents.report.biz.province;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.asynccomponents.report.biz.school.SchoolQuestScoreDetailBiz;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2017/2/20.
 */
@Service
public class ProvinceQuestScoreDetailBiz extends SchoolQuestScoreDetailBiz{

    @Autowired
    ProvinceService provinceService;

    @Override
    public Result execute(Param param) throws Exception {

        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");

        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));

        return getResultData(projectId, provinceRange, subjectId);
    }
}
