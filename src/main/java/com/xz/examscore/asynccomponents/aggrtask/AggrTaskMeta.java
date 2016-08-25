package com.xz.examscore.asynccomponents.aggrtask;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标注 {@link AggrTask} 的子类对应的任务类型
 *
 * @author yiding_he
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface AggrTaskMeta {

    /**
     * 获得该类适合处理的任务类型
     *
     * @return 任务类型
     */
    String taskType();
}
