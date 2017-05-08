package com.xz.examscore.asynccomponents.report.biz.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.asynccomponents.report.biz.classes.ClassQuestScoreDetailBiz;
import com.xz.examscore.bean.Range;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2017/2/17.
 */
@Service
public class SchoolQuestScoreDetailBiz extends ClassQuestScoreDetailBiz{

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String subjectId = param.getString("subjectId");

        Range range = Range.school(schoolId);

        return getResultData(projectId, range, subjectId);
    }

}
