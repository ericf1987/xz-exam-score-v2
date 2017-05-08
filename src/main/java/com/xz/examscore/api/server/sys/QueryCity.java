package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.*;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.CityService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Function(description = "查询行政区域", parameters = {
        @Parameter(name = "parent", type = Type.String, description = "上级区域ID", required = false, defaultValue = "000000")
}, result = @ResultInfo(listProperties =
@ListProperty(name = "items", description = "下级行政区域列表", properties = {
        @Property(name = "id", type = Type.String, description = "行政区域ID"),
        @Property(name = "parent", type = Type.String, description = "上级行政区域ID"),
        @Property(name = "name", type = Type.String, description = "名称")
})))
@Component
public class QueryCity implements Server {

    @Autowired
    CityService cityService;

    @Override
    public Result execute(Param param) throws Exception {
        String parent = param.getString("parent");
        List<Document> items = cityService.listItems(parent);

        return Result.success().set("items",
                CollectionUtils.convertList(items, document -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", document.getString("id"));
                    map.put("parent", document.getString("parent_id"));
                    map.put("name", document.getString("name"));
                    map.put("selectable", document.getString("selectable"));
                    return map;
                }));
    }
}