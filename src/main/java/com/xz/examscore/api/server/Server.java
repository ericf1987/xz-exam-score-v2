package com.xz.examscore.api.server;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;

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
    Result execute(Param param) throws Exception;
}
