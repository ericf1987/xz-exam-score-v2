package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.services.DictionaryService;
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
        List<Document> schoolTagsList = QuerySchoolTagsList(projectId);
        return Result.success().set("schoolTagsList", schoolTagsList);
    }

    //查询考试项目下所有tags
    private List<Document> QuerySchoolTagsList(String projectId) {
        List<Document> schoolList = schoolService.getProjectSchools(projectId);
        //获取所有Tags中的key
        List<String> keys = new ArrayList<>();
        schoolList.forEach(document -> {
            List<String> tags = (List<String>) document.get("tags");
            if (null != tags && !tags.isEmpty()) {
                tags.forEach(key ->{
                    if (!keys.contains(key))
                        keys.add(key);
                });
            }
        });
        //根据tags中的key获取字典项
        return getDictionariesByTypes(keys);
    }

    private List<Document> getDictionariesByTypes(List<String> types){
        String[] arrs = new String[types.size()];
        types.toArray(arrs);
        List<Document> tags = schoolTagService.findTagsByKeys(arrs);
        return tags;
    }

}
