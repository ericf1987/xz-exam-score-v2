package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/1/11.
 */
@Function(description = "联考项目-学校分数分段统计（10分段）", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "max", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "min", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "span", type = Type.String, description = "考试项目ID", required = true)})
@Service
public class TotalScoreMinAboveAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ScoreSegmentService scoreSegmentService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    ProjectService projectService;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    MinMaxScoreService minMaxScoreService;

    @Autowired
    TotalScoreSegmentCountAnalysis totalScoreSegmentCountAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String max = param.getString("max");
        String min = param.getString("min");
        String span = param.getString("span");
        List<String> column = getColumn(Integer.parseInt(max), Integer.parseInt(min), Integer.parseInt(span));
        Map<String, Object> provinceData = getProvinceData(projectId, Integer.parseInt(max), Integer.parseInt(min), Integer.parseInt(span));
        List<Map<String, Object>> schoolData = getSchoolsData(projectId, Integer.parseInt(max), Integer.parseInt(min), Integer.parseInt(span));
        return Result.success().set("column", column).set("provinceData", provinceData).set("schoolData", schoolData);
    }

    private List<String> getColumn(int max, int min, int span) {
        List<String> column = new ArrayList<>();
        for (int i = max; i >= min; i -= span) {
            column.add(">=" + i);
        }
        column.add(min + "以下");
        return column;
    }

    private Map<String, Object> getProvinceData(String projectId, int max, int min, int span) {
        String province = provinceService.getProjectProvince(projectId);
        Range provinceRange = Range.province(province);
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, provinceRange, Target.project(projectId));
        double maxScore = minMaxScore[1];
        CounterMap<Integer> data = getCounterMap(projectId, provinceRange, max, min, span);
        int count = studentService.getStudentCount(projectId, provinceRange, Target.project(projectId));
        Map<String, Object> map = new HashMap<>();
        map.put("max", maxScore);
        map.put("count", count);
        map.put("data", data);
        map.put("name", "总体");
        map.put("id", province);
        return map;
    }

    private CounterMap<Integer> getCounterMap(String projectId, Range range, int max, int min, int span) {
        TotalScoreSegmentCountAnalysis.ScoreSegmentCounter scoreSegmentCounter = totalScoreSegmentCountAnalysis.new ScoreSegmentCounter(projectId, range, max, min, span);
        int minScore = scoreSegmentCounter.getMin();
        int interval = scoreSegmentCounter.getInterval();
        int maxScore = scoreSegmentCounter.getMax();
        for (int i = maxScore; i >= minScore; i -= interval) {
            int countByScoreSpan = scoreService.getCountByScoreSpan(projectId, range, Target.project(projectId), 0, i);
            scoreSegmentCounter.addToCounter(i, countByScoreSpan);
        }
        int countByScoreSpan = scoreService.getCountByScoreSpan(projectId, range, Target.project(projectId), min, 0);
        scoreSegmentCounter.addToCounter(0, countByScoreSpan);
        return scoreSegmentCounter.getCounterMap();
    }

    private List<Map<String, Object>> getSchoolsData(String projectId, int max, int min, int span) {
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        List<Map<String, Object>> result = new ArrayList<>();
        for(Document schoolDoc : projectSchools){
            String schoolId = schoolDoc.getString("school");
            Range schoolRange = Range.school(schoolId);
            double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, schoolRange, Target.project(projectId));
            double maxScore = minMaxScore[1];
            int count = studentService.getStudentCount(projectId, schoolRange, Target.project(projectId));
            Map<String, Object> map = new HashMap<>();
            map.put("max", maxScore);
            map.put("count", count);
            map.put("id", schoolId);
            map.put("name", schoolService.getSchoolName(projectId, schoolId));
            map.put("data", getCounterMap(projectId, schoolRange, max, min, span));
            result.add(map);
        }
        return result;
    }

}
