package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectSubjectiveAnalysis;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/6/15.
 */
@Component
public class TotalPaperSubjectiveSheets extends SheetGenerator {

    @Autowired
    ProjectSubjectiveAnalysis projectSubjectiveAnalysis;

    @Autowired
    SchoolService schoolService;

    public static final String[] SECONDARY_COLUMN = new String[]{
            "分数", "得分率"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();

        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().
                map(d -> d.getString("school")).collect(Collectors.toList());
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));
        Result result = projectSubjectiveAnalysis.execute(param);
        //LOG.debug("主观题统计-->{}", result.getData());
        //System.out.println("主观题统计-->{}" + result.getData());
/*        setupHeader(excelWriter, result.get("totals"));
        setupSecondaryHeader(excelWriter, result.get("totals"));
        fillProvinceData(result.get("totals"), excelWriter);
        fillSchoolData(result.getList("schools", null),excelWriter);*/
        setupHeader(excelWriter, result.get("schools"));
        setupSecondaryHeader(excelWriter,result.get("schools"));
        fillData(excelWriter, result);
    }

    @SuppressWarnings("unchecked")
    private void fillData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> totals = result.get("totals");
        List<Map<String, Object>> schools = result.get("schools");
        for(int i = 0;i < totals.size();i++){
            excelWriter.set(2 + i, column.incrementAndGet(), totals.get(i).get("questNo").toString());
            excelWriter.set(2 + i, column.incrementAndGet(), totals.get(i).get("score").toString());
            excelWriter.set(2 + i, column.incrementAndGet(), totals.get(i).get("rate").toString());
            for(Map<String, Object> school : schools){
                List<Map<String, Object>> subjectives = (List<Map<String, Object>>)school.get("subjectives");
                excelWriter.set(2 + i, column.incrementAndGet(), subjectives.get(i).get("score").toString());
                excelWriter.set(2 + i, column.incrementAndGet(), subjectives.get(i).get("rate").toString());
            }
            column.set(-1);
        }
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "主观题");
        excelWriter.set(0, column.incrementAndGet(), "总体");
        excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        for(Map<String, Object> school : schools){
            excelWriter.set(0, column.incrementAndGet(), school.get("schoolName").toString());
            excelWriter.mergeCells(0, column.get(), 0, column.incrementAndGet());
        }
    }

    public void setupSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "主观题");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[0]);
        excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[1]);
        for (int i = 0; i < schools.size(); i++){
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_COLUMN[1]);
        }
    }


}
