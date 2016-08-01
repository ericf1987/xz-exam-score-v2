package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.api.server.classes.ClassRankLevelAnalysis;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/7/18.
 */
@SuppressWarnings("unchecked")
@Function(description = "学校成绩-等第排名统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Service
public class SchoolRankLevelAnalysis implements Server{

    @Autowired
    TargetService targetService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ClassRankLevelAnalysis classRankLevelAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");

        Target target = targetService.getTarget(projectId, subjectId);

        Map<String, Object> school = getSchoolMap(projectId, target, schoolId);
        List<Map<String, Object>> classes = getClassList(projectId, target, schoolId, subjectId);
        return Result.success().set("school", school).set("classes", classes);
    }

    private Map<String, Object> getSchoolMap(String projectId, Target target, String schoolId) {
        Range schoolRange =  Range.school(schoolId);
        Map<String, Object> schoolMap = new HashMap<>();
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        int studentCount = studentService.getStudentCount(projectId, Range.school(schoolId));
        //统计校级等第
        List<Map<String, Object>> rankLevels = sort(rankLevelService.getRankLevelMap(projectId, schoolRange, target));
        schoolMap.put("studentCount", studentCount);
        schoolMap.put("schoolName", schoolName);
        schoolMap.put("schoolId", schoolId);
        schoolMap.put("rankLevels", rankLevels);
        return schoolMap;
    }

    private List<Map<String, Object>> getClassList(String projectId, Target target, String schoolId, String subjectId){
        List<String> classIds = classService.listClasses(projectId, schoolId).stream()
                .map(document -> document.getString("class")).collect(Collectors.toList());

        //查询考试配置表中的等级配置参数
        List<String> rankLevelParam = classRankLevelAnalysis.getRankLevelParams(projectId, subjectId);

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        String lastRankLevel = projectConfig.getLastRankLevel();

        List<Map<String, Object>> classList = new ArrayList<>();

        for(String classId : classIds){
            List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
            CounterMap map = new CounterMap();
            //统计每个班级中的学生在学校范围内的等第情况，并做汇总
            for(Document doc : studentList){
                String studentId = doc.getString("student");
                String subjectRankLevel = rankLevelService.getRankLevel(projectId, studentId, target, Range.SCHOOL, lastRankLevel);
                map.incre(subjectRankLevel);
            }

            //将CountMap做格式转化
            List<Map<String, Object>> rankLevelList = convert(rankLevelParam, map);
            Map<String, Object> clazzMap = new HashMap<>();
            clazzMap.put("rankLevels", rankLevelList);
            clazzMap.put("className", classService.getClassName(projectId, classId));
            clazzMap.put("classId", classId);
            classList.add(clazzMap);
        }
        return classList;
    }

    private List<Map<String, Object>> convert(List<String> rankLevelParam, CounterMap map) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String rankLevel : rankLevelParam){
            Map<String, Object> m = new HashMap<>();
            m.put("rankLevel", format(rankLevel));
            m.put("count", map.get(format(rankLevel)) == null ? 0 : map.get(format(rankLevel)));
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> sort(List<Map<String, Object>> list){
        Collections.sort(list, (Map<String, Object> m1, Map<String, Object> m2) ->
            m1.get("rankLevel").toString().compareTo(m2.get("rankLevel").toString())
        );
        return list;
    }

    //格式化等第参数 例如将4A1B1C转化成AAAABC
    public String format(String str) {
        StringBuilder builder = new StringBuilder();

        //匹配数字
        Pattern p_number = Pattern.compile("\\d+");

        //匹配字母
        Pattern p_char = Pattern.compile("[a-zA-Z]+");
        String[] numbers = p_char.split(str);
        String[] chars = p_number.split(str);

        if (numbers.length == 0) {
            return str;
        } else {
            for (int i = 0; i < numbers.length; i++) {
                int pos = Integer.parseInt(numbers[i]);
                for (int j = 0; j < pos; j++) {
                    builder.append(chars[i + 1]);
                }
            }
            return builder.toString();
        }
    }

}
