package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
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

import java.util.*;

/**
 * @author by fengye on 2016/12/3.
 */
@Function(description = "联考项目-学校分数分段统计（10分段）", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "max", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "min", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "span", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class TotalScoreSegmentCountAnalysis implements Server {

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

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String max = param.getString("max");
        String min = param.getString("min");
        String span = param.getString("span");
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        Map<String, Object> projectScoreSegmentMap = getProjectScoreSegment(projectId, Integer.parseInt(max), Integer.parseInt(min), Integer.parseInt(span));
        List<Map<String, Object>> schoolScoreSegmentList = getSchoolScoreSegment(projectId, projectSchools, Integer.parseInt(max), Integer.parseInt(min), Integer.parseInt(span));
        List<String> column = generateColumn(Integer.parseInt(max), Integer.parseInt(min), Integer.parseInt(span));
        return Result.success().set("project", projectScoreSegmentMap).set("schools", schoolScoreSegmentList).set("column", column);
    }

    private List<String> generateColumn(int max, int min, int span) {
        List<String> column = new ArrayList<>();
        column.add(min + "以下");
        for (int i = min; i < max; i += span){
            int j = i + span;
            column.add(i + "-" + j);
        }
        column.add(max + "以上");
        return column;
    }

    private Map<String, Object> getProjectScoreSegment(String projectId, int max, int min, int span) {
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        CounterMap<Integer> counterMap = getScoreSegmentMap(projectId, provinceRange, max, min, span);
        Map<String, Object> provinceData = new HashMap<>();
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, provinceRange, Target.project(projectId));
        provinceData.put("name", "总体");
        provinceData.put("count", studentService.getStudentCount(projectId, provinceRange, Target.project(projectId)));
        provinceData.put("max", minMaxScore[1]);
        provinceData.put("data", counterMap);
        return provinceData;
    }

    private List<Map<String, Object>> getSchoolScoreSegment(String projectId, List<Document> projectSchools, int max, int min, int span) {
        List<Map<String, Object>> schoolsData = new ArrayList<>();
        for (Document schoolDoc : projectSchools) {
            String schoolId = schoolDoc.getString("school");
            Range schoolRange = Range.school(schoolId);
            CounterMap<Integer> counterMap = getScoreSegmentMap(projectId, schoolRange, max, min, span);
            double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, schoolRange, Target.project(projectId));
            Map<String, Object> schoolMap = new HashMap<>();
            schoolMap.put("name", schoolService.getSchoolName(projectId, schoolId));
            schoolMap.put("count", studentService.getStudentCount(projectId, schoolRange, Target.project(projectId)));
            schoolMap.put("max", minMaxScore[1]);
            schoolMap.put("data", counterMap);
            schoolsData.add(schoolMap);
        }
        return schoolsData;
    }

    private CounterMap<Integer> getScoreSegmentMap(String projectId, Range range, int maxScore, int minScore, int span) {
        ScoreSegmentCounter scoreSegmentCounter = new ScoreSegmentCounter(projectId, range, maxScore, minScore, span);
        int min = scoreSegmentCounter.getMin();
        int interval = scoreSegmentCounter.getInterval();
        int max = scoreSegmentCounter.getMax();
        //查询学生
        int minCount = scoreService.getCountByScoreSpan(projectId, range, Target.project(projectId), min, 0);
        scoreSegmentCounter.addToCounter(0, minCount);
        for (int i = min; i < max; i += interval) {
            int count = scoreService.getCountByScoreSpan(projectId, range, Target.project(projectId), i + interval, i);
            scoreSegmentCounter.addToCounter(i, count);
        }
        int maxCount = scoreService.getCountByScoreSpan(projectId, range, Target.project(projectId), 0, max);
        scoreSegmentCounter.addToCounter(max, maxCount);
        return scoreSegmentCounter.getCounterMap();
    }

    public class ScoreSegmentCounter {
        private String projectId;

        private Range range;

        private int max;

        private int min;

        private int interval;

        private CounterMap<Integer> counterMap = new CounterMap<>();

        public ScoreSegmentCounter(String projectId, Range range, int max, int min, int interval) {
            this.projectId = projectId;
            this.range = range;
            this.max = max;
            this.min = min;
            this.interval = interval;
        }

        public ScoreSegmentCounter(String projectId, int max, int min, int interval) {
            this.projectId = projectId;
            this.max = max;
            this.min = min;
            this.interval = interval;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public Range getRange() {
            return range;
        }

        public void setRange(Range range) {
            this.range = range;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public void addToCounter(double score) {
            int key = ((int) (score / interval)) * interval;
            counterMap.incre(key);
        }

        public void addToCounter(double score, int count) {
            int key = ((int) (score / interval)) * interval;
            counterMap.incre(key, count);
        }

        public CounterMap<Integer> getCounterMap() {
            return counterMap;
        }

        public List<String> getSpan() {
            return null;
        }
    }

}
