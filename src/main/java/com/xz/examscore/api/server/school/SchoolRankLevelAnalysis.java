package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassRankLevelAnalysis;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
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
public class SchoolRankLevelAnalysis implements Server {

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

        //查询考试配置表中的等级配置参数
        List<String> rankLevelParam = classRankLevelAnalysis.getRankLevelParams(projectId, subjectId);
        Map<String, Double> rankLevelsConfig = projectConfigService.getProjectConfig(projectId).getRankLevels();
        Map<String, Object> school = getSchoolMap(projectId, target, schoolId, rankLevelParam);
        List<Map<String, Object>> classes = getClassList(projectId, target, schoolId, rankLevelParam, school);
        return Result.success().set("school", school).set("classes", classes).set("rankLevelParam", rankLevelParam)
                .set("hasHeader", !rankLevelParam.isEmpty())
                .set("rankLevelsConfig", rankLevelsConfig);
    }

    //查询学校等第数据
    private Map<String, Object> getSchoolMap(String projectId, Target target, String schoolId, List<String> rankLevelParam) {
        Range schoolRange = Range.school(schoolId);
        Map<String, Object> schoolMap = new HashMap<>();
        String schoolName = schoolService.getSchoolName(projectId, schoolId);
        int studentCount = studentService.getStudentCount(projectId, Range.school(schoolId));

        //获取给定等第配置参数对应的人数
        List<Map<String, Object>> rankLevelList = rankLevelService.getRankLevelMap(projectId, schoolRange, target);
        CounterMap inMap = new CounterMap();
        for (String param : rankLevelParam) {
            rankLevelList.forEach(rankLevel -> {
                if (format(param).equals(rankLevel.get("rankLevel").toString())) {
                    inMap.incre(format(param), Integer.parseInt(rankLevel.get("count").toString()));
                }
            });
        }

        List<Map<String, Object>> rankLevels = sort(convert(rankLevelParam, inMap, studentCount));

        //其他排名段的学生总数
        int others = studentCount - getCountInRankLevel(rankLevels);

        //等第占比
        schoolMap.put("studentCount", studentCount);
        schoolMap.put("schoolName", schoolName);
        schoolMap.put("schoolId", schoolId);
        Map<String, Object> othersMap = new HashMap<>();
        othersMap.put("count", others);
        othersMap.put("rate", DoubleUtils.round((double) others / studentCount, true));
        schoolMap.put("others", othersMap);
        schoolMap.put("rankLevels", rankLevels);
        return schoolMap;
    }

    private List<Map<String, Object>> getClassList(String projectId, Target target, String schoolId, List<String> rankLevelParam, Map<String, Object> school) {
        List<String> classIds = classService.listClasses(projectId, schoolId).stream()
                .map(document -> document.getString("class")).collect(Collectors.toList());

        //int studentCount = studentService.getStudentCount(projectId, Range.school(schoolId));

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        String lastRankLevel = projectConfig.getLastRankLevel();

        List<Map<String, Object>> classList = new ArrayList<>();

        for (String classId : classIds) {
            int studentCount = studentService.getStudentCount(projectId, Range.clazz(classId));
            List<Document> studentList = studentService.getStudentList(projectId, Range.clazz(classId));
            CounterMap map = new CounterMap();
            //统计每个班级中的学生在学校范围内的等第情况，并做汇总
            for (Document doc : studentList) {
                String studentId = doc.getString("student");
                String subjectRankLevel = rankLevelService.getRankLevel(projectId, studentId, target, Range.SCHOOL, lastRankLevel);
                map.incre(subjectRankLevel);
            }

            //将CountMap做格式转化
            List<Map<String, Object>> rankLevels = sort(convert0(rankLevelParam, map, school));
            //给定等第参数下对应的人数总和
            int rankLevelCount = getCountInRankLevel(rankLevels);
            int others = studentCount - rankLevelCount;
            Map<String, Object> clazzMap = new HashMap<>();
            clazzMap.put("rankLevels", rankLevels);
            clazzMap.put("className", classService.getClassName(projectId, classId));
            clazzMap.put("classId", classId);
            clazzMap.put("studentCount", studentCount);
            Map<String, Object> othersMap = new HashMap<>();
            othersMap.put("count", others);
            othersMap.put("rate", DoubleUtils.round((double) others / studentCount, true));
            clazzMap.put("others", othersMap);
            classList.add(clazzMap);
        }
        return classList;
    }

    private int getCountInRankLevel(List<Map<String, Object>> rankLevels) {
        int count = 0;
        for (Map<String, Object> rankLevel : rankLevels) {
            int c = Integer.parseInt(rankLevel.get("count").toString());
            count += c;
        }
        return count;
    }

    private List<Map<String, Object>> convert(List<String> rankLevelParam, CounterMap map, int studentCount) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String rankLevel : rankLevelParam) {
            int count = map.get(format(rankLevel)) == null ? 0 : Integer.parseInt(map.get(format(rankLevel)).toString());
            Map<String, Object> m = new HashMap<>();
            m.put("rankLevel", format(rankLevel));
            m.put("count", count);
            m.put("rate", DoubleUtils.round(getRate2(count, studentCount), true));
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> convert0(List<String> rankLevelParam, CounterMap map, Map<String, Object> school) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> studentRankLevels = (List<Map<String, Object>>) school.get("rankLevels");
        for (int i = 0; i < rankLevelParam.size(); i++) {
            int count = map.get(format(rankLevelParam.get(i))) == null ? 0 : Integer.parseInt(map.get(format(rankLevelParam.get(i))).toString());
            //班级所在学校在该等第内的人数
            int studentCount = Integer.parseInt(studentRankLevels.get(i).get("count").toString());
            Map<String, Object> m = new HashMap<>();
            m.put("rankLevel", format(rankLevelParam.get(i)));
            m.put("count", count);
            m.put("studentCount", studentCount);
            m.put("rate", DoubleUtils.round(getRate2(count, studentCount), true));
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> sort(List<Map<String, Object>> list) {
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
        //["4", "1", "1"]
        String[] numbers = p_char.split(str);
        //["", "A", "B", "C"]
        String[] chars = p_number.split(str);

        if (numbers.length == 0) {
            return str;
        } else {
            for (int i = 0; i < numbers.length; i++) {
                int pos = Integer.parseInt(numbers[i]);
                for (int j = 0; j < pos; j++)
                    builder.append(chars[i + 1]);
            }
            return builder.toString();
        }
    }

    //格式化等第参数 例如将AAAABC转化成4A1B1C
    public String format2(String str){
        //数组存放对应26个字母的出现次数比如a[0]的值对应字母A出现的次数，a[2]的值对应C出现的次数。。。
        int[] arr = new int[26];

        for(int i = 0; i < str.length();i++){
            char c = str.charAt(i);
            int index = c - 'A';
            arr[index] = arr[index] + 1;
        }

        StringBuilder builder = new StringBuilder();
        for(int j = 0; j < arr.length; j++){
            if(arr[j] != 0){
                builder.append(arr[j]).append("").append((char) (j + 'A'));
            }
        }
        return builder.toString();
    }

    public double getRate2(int count, int studentCount) {
        if (studentCount == 0) {
            return 0;
        }
        return (double) count / studentCount;
    }

}
