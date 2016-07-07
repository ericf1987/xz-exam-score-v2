package com.xz.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.RangeService;
import com.xz.services.SchoolService;
import com.xz.services.SubjectService;
import com.xz.services.TopAverageService;
import com.xz.util.DoubleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.services.SubjectService.getSubjectName;

/**
 * 总体成绩-高分段竞争力分析
 *
 * @author zhaorenwu
 */

@Function(description = "总体成绩-高分段竞争力分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true),
        @Parameter(name = "percent", type = Type.Decimal, description = "总分前百分比", required = false, defaultValue = "0.3"),
})
@Service
public class ProjectHighSegmentAnalysis implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectHighSegmentAnalysis.class);

    @Autowired
    TopAverageService topAverageService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    RangeService rangeService;

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String[] schoolIds = param.getStringValues("schoolIds");
        double percent = param.getDouble("percent");

        List<Map<String, Object>> schoolHighSegmentAnalysis =
                getSchoolHighSegmentAnalysis(projectId, schoolIds, percent);
        List<Map<String, Object>> totalHighSegmentAnalysis =
                getTotalHighSegmentAnalysis(projectId, percent);

        return Result.success()
                .set("totals", totalHighSegmentAnalysis)
                .set("schools", schoolHighSegmentAnalysis)
                .set("hasHeader", true);
    }

    // 学校高分段竞争力分析
    private List<Map<String, Object>> getSchoolHighSegmentAnalysis(String projectId, String[] schoolIds, double percent) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("schoolName", schoolName);

            Range range = Range.school(schoolId);
            List<Map<String, Object>> subjects = getHighSegmentAnalysis(projectId, range, percent,
                    subjectService, topAverageService);
            map.put("subjects", subjects);

            list.add(map);
        }

        return list;
    }

    // 总体高分段竞争力分析
    private List<Map<String, Object>> getTotalHighSegmentAnalysis(String projectId, double percent) {
        Range range = rangeService.queryProvinceRange(projectId);
        return getHighSegmentAnalysis(projectId, range, percent, subjectService, topAverageService);
    }

    // 高分段竞争力（各校总分前30%）分析
    public static List<Map<String, Object>> getHighSegmentAnalysis(String projectId, Range range, double percent,
                                                                   SubjectService subjectService,
                                                                   TopAverageService topAverageService) {
        // 各科分析
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> subjectIds = subjectService.querySubjects(projectId);
        for (String subjectId : subjectIds) {
            Map<String, Object> subjectInfo = new HashMap<>();

            double topAverage = topAverageService.getTopAverage(projectId, Target.subject(subjectId), range, percent);
            subjectInfo.put("average", DoubleUtils.round(topAverage));

            subjectInfo.put("subjectId", subjectId);
            subjectInfo.put("subjectName", getSubjectName(subjectId));
            list.add(subjectInfo);
        }

        // 总分分析
        Map<String, Object> projectInfo = new HashMap<>();
        double topAverage = topAverageService.getTopAverage(projectId, Target.project(projectId), range, percent);
        projectInfo.put("average", DoubleUtils.round(topAverage));
        projectInfo.put("subjectId", "000");
        projectInfo.put("subjectName", "总体");
        list.add(projectInfo);

        list.sort((o1, o2) -> ((String) o1.get("subjectId")).compareTo((String) o2.get("subjectId")));
        return list;
    }
}
