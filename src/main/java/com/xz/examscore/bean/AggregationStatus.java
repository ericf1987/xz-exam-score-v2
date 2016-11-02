package com.xz.examscore.bean;

/**
 * @author by fengye on 2016/11/2.
 */
public enum AggregationStatus {
    /**
     * 数据库中没有找到该项目
     */
    Empty,

    /**
     * 统计任务激活中
     */
    Activated,

    /**
     * 统计任务已经结束
     */
    Terminated
}
