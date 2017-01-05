package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.QuestAbilityLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by fengye on 2017/1/4.
 */
@Function(description = "联考项目-试卷难度系数", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class TotalQuestAbilityLevelAnalysis implements Server{

    @Autowired
    QuestAbilityLevelService questAbilityLevelService;

    @Override
    public Result execute(Param param) throws Exception {
        return null;
    }
}
