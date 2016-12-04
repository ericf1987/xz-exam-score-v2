package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ProjectService;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.ScoreSegmentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/12/3.
 */
@Function(description = "联考项目-分数段excel数据接口", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
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

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        ScoreSegmentAnalyzer analyzer = new ScoreSegmentAnalyzer(900, 300, 100);
        List<String> column = analyzer.getSpans(analyzer.getMax(), analyzer.getMin(), analyzer.getStepValue());
        Map<String, Object> projectScoreSegmentMap = getProjectScoreSegment(projectId, analyzer, column);
        List<Map<String, Object>> schoolScoreSegmentList = getSchoolScoreSegment(projectId, projectSchools, analyzer, column);
        Result result = Result.success().set("column", column).set("project", projectScoreSegmentMap).set("school", schoolScoreSegmentList);
        return result;
    }

    private List<Map<String, Object>> getSchoolScoreSegment(String projectId, List<Document> projectSchools, ScoreSegmentAnalyzer analyzer, List<String> column) {
        List<Map<String, Object>> schoolScoreSegmentList = new ArrayList<>();
        for(Document doc : projectSchools){
            String name = doc.getString("name");
            List<Document> scoreSegment = scoreSegmentService.getScoreSegment(projectId, Range.school(doc.getString("school")), Target.project(projectId));
            Map<String, Object> scoreSegmentMap = generateScoreSegment(name, scoreSegment, analyzer, column);
            schoolScoreSegmentList.add(scoreSegmentMap);
        }
        return schoolScoreSegmentList;
    }

    private Map<String, Object> getProjectScoreSegment(String projectId, ScoreSegmentAnalyzer analyzer, List<String> column) {
        String province = provinceService.getProjectProvince(projectId);
        List<Document> scoreSegment = scoreSegmentService.getScoreSegment(projectId, Range.province(province), Target.project(projectId));
        String name = projectService.findProject(projectId).getString("name");
        return generateScoreSegment(name, scoreSegment, analyzer, column);
    }

    private Map<String, Object> generateScoreSegment(String name, List<Document> scoreSegment, ScoreSegmentAnalyzer analyzer, List<String> column) {
        Map<String, Object> scoreSegmentMap = new HashMap<>();
        int max = analyzer.getMax();
        int min = analyzer.getMin();
        for (String item : column){
            if(item.contains(String.valueOf(max))){
                int totalCount = moreThenMax(scoreSegment, max);
                scoreSegmentMap.put(item, totalCount);
            }else if(item.contains(String.valueOf(min))){
                int totalCount = lessThenMin(scoreSegment, min);
                scoreSegmentMap.put(item, totalCount);
            }else{
                int end = Integer.valueOf(item.split("-")[0]);
                int start = Integer.valueOf(item.split("-")[1]);
                int totalCount = getTotalCountByMaxAndMin(scoreSegment, end, start);
                scoreSegmentMap.put(item, totalCount);
            }
        }
        return scoreSegmentMap;
    }

    private int lessThenMin(List<Document> scoreSegment, int min) {
        int count = 0;
        for(Document doc : scoreSegment){
            int segment = doc.getInteger("segment");
            if(segment < min){
                count += doc.getInteger("count");
            }
        }
        return count;
    }

    private int moreThenMax(List<Document> scoreSegment, int max) {
        int count = 0;
        for(Document doc : scoreSegment){
            int segment = doc.getInteger("segment");
            if(segment >= max){
                count += doc.getInteger("count");
            }
        }
        return count;
    }

    private int getTotalCountByMaxAndMin(List<Document> scoreSegment, int end, int start) {
        int count = 0;
        for(Document doc : scoreSegment){
            int segment = doc.getInteger("segment");
            if(segment < end && segment >= start){
                count += doc.getInteger("count");
            }
        }
        return count;
    }

    public class ScoreSegmentAnalyzer {
        private int max;

        private int min;

        private int stepValue;

        public ScoreSegmentAnalyzer(int max, int min, int stepValue) {
            this.max = max;
            this.min = min;
            this.stepValue = stepValue;
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

        public int getStepValue() {
            return stepValue;
        }

        public void setStepValue(int stepValue) {
            this.stepValue = stepValue;
        }

        public List<String> getSpans(int max, int min, int stepValue) {
            List<String> spans = new ArrayList<>();
            spans.add(String.valueOf(max) + "以上");
            int currentValue = max;
            while (currentValue > min) {
                spans.add(String.valueOf(currentValue) + "-" + String.valueOf(currentValue -= stepValue));
            }
            spans.add(String.valueOf(min) + "以下");
            return spans;
        }

    }
}
