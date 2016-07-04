package com.xz.taskdispatchers;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 任务相关属性
 *
 * @author yiding_he
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface TaskDispatcherInfo {

    String taskType();                      // 任务类型

    String dependentTaskType() default "";  // 依赖任务类型

    boolean isAdvanced() default false;
}
