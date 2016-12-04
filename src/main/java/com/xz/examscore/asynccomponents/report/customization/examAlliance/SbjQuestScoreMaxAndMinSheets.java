package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.customization.examAlliance.QuestScoreMaxAndMin;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/12/4.
 */
@Component
public class SbjQuestScoreMaxAndMinSheets extends SheetGenerator {

    @Autowired
    QuestScoreMaxAndMin questScoreMaxAndMin;

    @Autowired
    ObjQuestScoreMaxAndMinSheets objQuestScoreMaxAndMinSheets;

    public static final String[] HEADER = new String[]{
            "得分率最高的主观题", "得分率最低的主观题"
    };

    public static final String[] SECONDARY_HEADER = new String[]{
            "小题", "得分", "得分率", "对应知识点"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", target.getId().toString())
                .setParameter("isObjective", false);
        Result result = questScoreMaxAndMin.execute(param);
        objQuestScoreMaxAndMinSheets.setHeader(excelWriter, HEADER);
        objQuestScoreMaxAndMinSheets.setSecondaryHeader(excelWriter);
        objQuestScoreMaxAndMinSheets.fillData(excelWriter, result);
    }
}
