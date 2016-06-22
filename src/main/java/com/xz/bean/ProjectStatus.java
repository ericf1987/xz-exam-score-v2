package com.xz.bean;

/**
 * 项目状态
 * created at 16/06/22
 *
 * @author yiding_he
 */
public enum ProjectStatus {

    /**
     * 数据库中没有找到该项目
     */
    Empty,

    /**
     * 项目信息已经导入，但尚未进行任何统计
     */
    Imported,

    /**
     * 项目正在统计当中
     */
    AggregationStarted,

    /**
     * 最近一次的项目统计成功结束
     */
    AggregationCompleted,

    /**
     * 最近一次的项目统计失败
     */
    AggregationFailed
}
