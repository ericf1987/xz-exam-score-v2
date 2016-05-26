package com.xz.api.server;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;

/**
 * (描述)
 *
 * @author zhaorenwu
 */
public interface Server {

    /**
     * 服务处理
     *
     * @param param 参数信息
     *
     * @return 处理结果
     */
    public Result execute(Param param) throws Exception;
}
