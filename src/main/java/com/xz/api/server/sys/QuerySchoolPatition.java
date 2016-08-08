package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import com.xz.services.DictionaryService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/8/3.
 */
@Function(description = "根据考试项目ID查询考试科目列表",
        result = @ResultInfo(
                listProperties =
                @ListProperty(name = "schoolPatitionParams", description = "分区参数", properties = {
                        @Property(name = "key", type = Type.String, description = "分区键"),
                        @Property(name = "value", type = Type.String, description = "分区值"),
                        @Property(name = "desc", type = Type.String, description = "分区描述")
                })
        )
)
@Service
public class QuerySchoolPatition implements Server {

    @Autowired
    DictionaryService dictionaryService;

    //分区参数
    String[] types = new String[]{
            "isGovernmental", "isInCity"
    };

    @Override
    public Result execute(Param param) throws Exception {
        Map<String, List<Document>> params = new HashMap<>();
        for (String type : types) {
            params.put(type, querySchoolPatition(type));
        }
        return Result.success().set("schoolPatitionParams", params);
    }

    private List<Document> querySchoolPatition(String type) {
        return dictionaryService.listItems(type);
    }
}
