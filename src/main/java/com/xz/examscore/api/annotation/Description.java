package com.xz.examscore.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 描述信息
 *
 * @author zhaorenwu
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

    public String value();
}
