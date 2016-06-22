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
     * 正在导入项目信息
     */
    ProjectImporting,

    /**
     * 项目信息已经导入，但尚未进行任何统计
     */
    ProjectImported,

    /**
     * 正在导入成绩
     */
    ScoreImporting,

    /**
     * 项目成绩已经导入
     */
    ScoreImported,

    /**
     * 项目正在统计当中
     */
    AggregationStarted,

    /**
     * 最近一次的项目统计成功结束
     */
    AggregationCompleted,

    /**
     * 正在生成报表
     */
    ReportGenerating,

    /**
     * 报表已经生成
     */
    ReportGenerated,

    /**
     * 最近一次的项目统计失败
     */
    AggregationFailed
}
