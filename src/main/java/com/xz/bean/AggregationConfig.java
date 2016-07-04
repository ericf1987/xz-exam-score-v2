package com.xz.bean;

/**
 * 针对单次统计的选项配置
 * created at 16/07/04
 *
 * @author yiding_he
 */
public class AggregationConfig {

    private AggregationType aggregationType;

    private boolean reimportProject;

    private boolean reimportScore;

    private boolean generateReport;

    private boolean exportScore;

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(AggregationType aggregationType) {
        this.aggregationType = aggregationType;
    }

    public boolean isExportScore() {
        return exportScore;
    }

    public void setExportScore(boolean exportScore) {
        this.exportScore = exportScore;
    }

    public boolean isReimportProject() {
        return reimportProject;
    }

    public void setReimportProject(boolean reimportProject) {
        this.reimportProject = reimportProject;
    }

    public boolean isReimportScore() {
        return reimportScore;
    }

    public void setReimportScore(boolean reimportScore) {
        this.reimportScore = reimportScore;
    }

    public boolean isGenerateReport() {
        return generateReport;
    }

    public void setGenerateReport(boolean generateReport) {
        this.generateReport = generateReport;
    }
}
