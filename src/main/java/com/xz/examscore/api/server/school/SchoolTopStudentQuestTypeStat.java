package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectQuestTypeAnalysis.getQuestTypeAnalysis;
import static com.xz.examscore.api.server.project.ProjectTopStudentQuestTypeStat.getTopStudentQuestTypeStat;
import static com.xz.examscore.api.server.project.ProjectTopStudentQuestTypeStat.initRankSegment;

/**
 * 学校成绩-尖子试卷题型分析
 *
 * @author zhaorenwu
 */

@Function(description = "学校成绩-尖子生试卷题型分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "rankSegment", type = Type.StringArray, description = "排名分段,默认为第一分数段", required = false)
})
@Service
public class SchoolTopStudentQuestTypeStat implements Server {

    @Autowired
    TopStudentListService topStudentListService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    ScoreService scoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String schoolId = param.getString("schoolId");
        String[] rankSegment = param.getStringValues("rankSegment");

        Range range = Range.school(schoolId);
        Target target = Target.project(projectId);

        if (ArrayUtils.isEmpty(rankSegment)) {
            rankSegment = initRankSegment(projectId, range, topStudentListService);
        }

        List<Map<String, Object>> schoolQuestTypeAnalysis = getQuestTypeAnalysis(projectId, subjectId, range,
                questTypeService, fullScoreService, questTypeScoreService);

        List<Map<String, Object>> topStudents = getTopStudentQuestTypeStat(projectId, rankSegment, range, target,
                subjectId, topStudentListService, studentService, schoolService, classService,
                questTypeService, fullScoreService, questTypeScoreService, scoreService);
        return Result.success()
                .set("schools", schoolQuestTypeAnalysis)
                .set("topStudents", topStudents)
                .set("hasHeader", !schoolQuestTypeAnalysis.isEmpty());
    }
}
