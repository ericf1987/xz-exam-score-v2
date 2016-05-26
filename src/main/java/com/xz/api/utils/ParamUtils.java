package com.xz.api.utils;

import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.ApiException;
import com.xz.api.Param;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

/**
 * 参数处理工具类
 *
 * @author zhaorenwu
 */
public class ParamUtils {

    static final Logger LOG = LoggerFactory.getLogger(ParamUtils.class);

    /**
     * 解密参数信息
     *
     * @param p  待解密参数
     *
     * @return  结果
     */
    public static Param decipherParam(String p) {
        Param param = new Param();

        try {
            if (StringUtil.isEmpty(p)) {
                return param;
            }

            if (p.contains("%2F")) {
                p = URLDecoder.decode(p, "UTF-8");
            }

            //获取参数
            param = parseParams(p);

            LOG.info("the result after parsing is " + p);
            return param;
        }
        catch (Exception e) {

            throw new ApiException("params is error");
        }
    }

    private static Param parseParams(String value) {

        Param params = new Param();

        String[] segment = value.split(";");
        for (String aSegment : segment) {
            String paramKey = StringUtils.substringBefore(aSegment, "=");
            String paramValue = StringUtils.substringAfter(aSegment, "=");

            if (StringUtil.isNotBlank(paramValue)) {
                params.setParameter(paramKey, paramValue);
            }
        }

        return params;
    }
}
