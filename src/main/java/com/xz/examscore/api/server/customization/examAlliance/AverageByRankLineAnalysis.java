package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/12/5.
 */
@Function(description = "联考项目-前百分段名平均分", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "rankSegment", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class AverageByRankLineAnalysis implements Server {

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String rankSegment = param.getString("rankSegment");
        String province = provinceService.getProjectProvince(projectId);
        Range provinceRange = Range.province(province);
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        List<String> subjectIds = subjectService.querySubjects(projectId);

        //获取指定排名段前的人数
        Map<String, Object> resultMap = getResultData(projectId, provinceRange, rankSegment, projectSchools, subjectIds);

        return Result.success().set("subjectIds", subjectIds).set("provinceData", resultMap.get("provinceData"))
                .set("schoolsData", resultMap.get("schoolsData"));
    }

    private Map<String, Object> getResultData(String projectId, Range provinceRange, String rankSegment, List<Document> projectSchools, List<String> subjectIds) {
        Map<String, Object> map = new HashMap<>();
        //统计人数
        int studentCount = studentService.getStudentCount(projectId, provinceRange, Target.project(projectId));
        //得出占比人数
        int requiredCount = (int) (studentCount * Double.parseDouble(rankSegment));

        System.out.println("占比人数:" + requiredCount);

        //查出占比人数中最后一名对应的分数
        double rankScore = rankService.getRankScore(projectId, provinceRange, Target.project(projectId), requiredCount);

        List<Document> studentListByScore = scoreService.getListByMinScore(projectId, provinceRange, Target.project(projectId), rankScore);

        int totalSize = studentListByScore.size();

        Map<String, Object> provinceData = processData(projectId, studentListByScore, subjectIds, totalSize);

        List<Map<String, Object>> schoolsData = new ArrayList<>();

        for (Document schoolDoc : projectSchools) {
            String schoolId = schoolDoc.getString("school");
            Map<String, Object> schoolMap = processSchoolData(projectId, schoolId, studentListByScore, subjectIds, totalSize);
            schoolsData.add(schoolMap);
        }

        map.put("provinceData", provinceData);
        map.put("schoolsData", schoolsData);

        return map;
    }

    private Map<String, Object> processSchoolData(String projectId, String schoolId, List<Document> studentListByScore, List<String> subjectIds, int totalSize) {
        //过滤掉其他学校的数据
        List<Document> schoolsDoc = studentListByScore.stream().filter(student -> schoolId.equals(student.getString("school"))).collect(Collectors.toList());
        Map<String, Object> map = processData(projectId, schoolsDoc, subjectIds, totalSize);
        map.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
        return map;
    }

    private Map<String, Object> processData(String projectId, List<Document> studentListByScore, List<String> subjectIds, int totalSize) {
        Map<String, Object> map = new HashMap<>();
        List<String> studentIds = studentListByScore.stream().map(student -> {
            Document range = (Document) student.get("range");
            return range.getString("id");
        }).collect(Collectors.toList());
        //统计考试总体平均分
        int count = studentIds.size();
        map.put("count", count);
        map.put("rate", DoubleUtils.round((double)count / totalSize));

        List<Map<String, Object>> averageData = new ArrayList<>();
        Map<String, Object> projectData = new HashMap<>();
        double projectTotalScore = studentListByScore.stream().mapToDouble(student -> student.getDouble("totalScore")).sum();
        double projectAverage = DoubleUtils.round(projectTotalScore / count);
        projectData.put("subjectId", projectId);
        projectData.put("subjectName", "全科");
        projectData.put("average", projectAverage);
        averageData.add(projectData);

        List<AverageByRankLineExecutor> executors = new ArrayList<>();

        doExecute(projectId, studentIds, count, subjectIds, executors, averageData);

        map.put("averageData", averageData);
        return map;
    }

    private void doExecute(String projectId, List<String> studentIds, int count, List<String> subjectIds, List<AverageByRankLineExecutor> executors, List<Map<String, Object>> averageData) {
        for(String subjectId : subjectIds){
            AverageByRankLineExecutor executor = new AverageByRankLineExecutor(projectId, studentIds, count, subjectId);
            executor.start();
            executors.add(executor);
        }

        for (AverageByRankLineExecutor executor : executors){
            try {
                executor.join();
                averageData.add(executor.getResultMap());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Object> getAverageExecute(String projectId, List<String> studentIds, int count, String subjectId) {
        Map<String, Object> subjectData = new HashMap<>();
        double subjectTotalScore = 0;
        for (String studentId : studentIds) {
            double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
            subjectTotalScore += totalScore;
        }
        double subjectAverage = DoubleUtils.round(subjectTotalScore / count);
        subjectData.put("subjectId", subjectId);
        subjectData.put("subjectName", SubjectService.getSubjectName(subjectId));
        subjectData.put("average", subjectAverage);
        return subjectData;
    }

    private class AverageByRankLineExecutor extends Thread{
        private String projectId;
        private String subjectId;
        private int count;
        private List<String> studentIds;

        private Map<String, Object> resultMap = new HashMap<>();

        public AverageByRankLineExecutor(String projectId, List<String> studentIds, int count, String subjectId) {
            this.projectId = projectId;
            this.studentIds = studentIds;
            this.count = count;
            this.subjectId = subjectId;
        }

        public Map<String, Object> getResultMap() {
            return resultMap;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public List<String> getStudentIds() {
            return studentIds;
        }

        public void setStudentIds(List<String> studentIds) {
            this.studentIds = studentIds;
        }

        @Override
        public void run() {
            resultMap.putAll(getAverageExecute(projectId, studentIds, count, subjectId));
        }
    }
}
