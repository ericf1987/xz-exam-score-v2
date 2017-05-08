package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/10/16.
 */

@Function(description = "考试下所有班级成绩对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class AllClassScoreCompare implements Server{
    @Autowired
    SubjectService subjectService;
    
    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    AverageService averageService;

    @Autowired
    RankService rankService;

    @Autowired
    ProvinceService provinceService;

    @Override
    public Result execute(Param param) throws Exception {
        //查询出所有学校
        String projectId = param.getString("projectId");
        //获取所有学校
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        List<Document> allClasses = new ArrayList<>();
        List<Map<String, Object>> classes = new ArrayList<>();
        //获取所有班级ID
        projectSchools.forEach(
                school -> {
                    String schoolId = school.getString("school");
                    allClasses.addAll(CollectionUtils.asArrayList(classService.listClasses(projectId, schoolId)));
                }
        );
        //所有科目ID列表
        List<String> subjectIds = subjectService.querySubjects(projectId);
        //班级ID列表
        List<String> classIdList = allClasses.stream().map(c -> c.getString("class")).collect(Collectors.toList());
        //所有班级的平均分和排名
        List<Map<String, Object>> list = getRankBySubject(projectId, classIdList, subjectIds);
        for (Document clazz : allClasses){
            String classId = clazz.getString("class");
            String schoolId = clazz.getString("school");
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            Map<String, Object> classMap = new HashMap<>();
            List<Map<String, Object>> subjectsList = new ArrayList<>();
            subjectIds.forEach(
                    subjectId -> {
                        Map<String, Object> subjectInfo = getRankAndAverByClass(list, classId, subjectId);
                        subjectsList.add(subjectInfo);
                    }
            );
            double totalAverage = DoubleUtils.round(averageService.getAverage(projectId, Range.clazz(classId), Target.project(projectId)), false);
            classMap.put("schoolName", schoolName);
            classMap.put("className", clazz.getString("name"));
            classMap.put("subjects", subjectsList);
            classMap.put("totalAverage", totalAverage);
            classes.add(classMap);
        }
        paddingRank(classes, "totalAverage", "totalRank");

        return Result.success().set("classes", classes);
    }

    public List<Map<String, Object>> getRankBySubject(String projectId, List<String> classIds, List<String> subjectIds){
        List<Map<String, Object>> result = new ArrayList<>();
        for(String subjectId : subjectIds){
            List<Map<String, Object>> subjects = new ArrayList<>();
            for(String classId : classIds){
                Map<String, Object> map = new HashMap<>();
                double average = DoubleUtils.round(averageService.getAverage(projectId, Range.clazz(classId), Target.subject(subjectId)), false);
                map.put("subjectId", subjectId);
                map.put("classId", classId);
                map.put("average", average);
                subjects.add(map);
            }
            paddingRank(subjects, "average", "rank");
            result.addAll(subjects);
        }
        return result;
    }

    public Map<String, Object> getRankAndAverByClass(List<Map<String, Object>> list, String classId, String subjectId){
        if(!list.isEmpty()){
            for (Map<String, Object> m : list){
                if(m.get("classId").toString().equals(classId) && m.get("subjectId").toString().equals(subjectId)){
                    Map<String, Object> mm = new HashMap<>();
                    mm.put("subjectId", subjectId);
                    mm.put("subjectName", SubjectService.getSubjectName(subjectId));
                    mm.put("average", m.get("average"));
                    mm.put("rank", m.get("rank"));
                    return mm;
                }
            }
        }
        return Collections.emptyMap();
    }

    //追加排名
    private void paddingRank(List<Map<String, Object>> list, String average, String rank) {
        Collections.sort(list, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double d1 = Double.parseDouble(m1.get(average).toString());
            Double d2 = Double.parseDouble(m2.get(average).toString());
            return d2.compareTo(d1);
        });
        for(int i = 0;i < list.size();i++){
            Map<String, Object> m = list.get(i);
            m.put(rank, i + 1);
        }
    }
}
