package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Result execute(Param param) throws Exception {
        String parent = param.getString("parent");

        List<Map<String, Object>> items = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("id", "430000");
        item.put("parent", parent);
        item.put("name", "湖南");
        items.add(item);

        return Result.success().set("items", items);
    }
}