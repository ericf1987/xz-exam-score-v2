package com.xz.api.annotation;

/**
 * 用于描述返回值
 *
 * @author zhaorenwu
 */
public @interface ResultInfo {

    // 描述 success 属性
    String success() default "true 表示成功，false 表示失败";

    // 单行返回值属性
    Property[] properties() default {};

    // 多行返回值属性
    ListProperty[] listProperties() default {};
}
