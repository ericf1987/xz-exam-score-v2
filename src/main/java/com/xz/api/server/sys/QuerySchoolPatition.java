package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.services.SchoolService;
import com.xz.services.SchoolTagService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/8/3.
 */
@SuppressWarnings("unchecked")
@Function(description = "根据考试项目ID该考试所有学校的标签",
        parameters = {
                @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
        }
)
@Service
public class QuerySchoolPatition implements Server {

    @Autowired
    SchoolTagService schoolTagService;

    @Autowired
    SchoolService schoolService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<Map<String, Object>> schoolTagsList = querySchoolTagsList(projectId);
        return Result.success().set("schoolTagsList", schoolTagsList);
    }

    private List<Map<String, Object>> querySchoolTagsList(String projectId) {
        List<Document> tags = schoolService.findSchoolIdsByTags(projectId);
        List<Map<String, Object>> schoolTags = new ArrayList<>();
        for (Document tag : tags) {
            List<String> tagNames = new ArrayList();
            List<String> tagIds = (List<String>) tag.get("_id");
            for (String tagId : tagIds) {
                tagNames.add(schoolTagService.findTagNameByKey(tagId));
            }
            List<Map<String, String>> schoolList = new ArrayList<>();
            for (String schoolId : (List<String>) tag.get("schoolIds")) {
                Map<String, String> schoolMap = new HashMap<>();
                schoolMap.put("schoolId", schoolId);
                schoolMap.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
                schoolList.add(schoolMap);
            }
            Map<String, Object> oneTag = new HashMap<>();
            oneTag.put("tagNames", list2string(tagNames));
            oneTag.put("schools", schoolList);
            schoolTags.add(oneTag);
        }
        return schoolTags;
    }

    private String list2string(List<String> tagNames) {
        StringBuilder builder = new StringBuilder();
        tagNames.forEach(tagName -> builder.append(tagName).append(","));
        String result = builder.toString();
        return result.substring(0, result.length() - 1);
    }

}
