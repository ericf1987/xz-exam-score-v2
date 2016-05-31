package com.xz.report;

/**
 * (description)
 * created at 16/05/31
 *
 * @author yiding_he
 */
public class ReportTask {

    private ReportGenerator reportGenerator;

    private String category;

    private String filename;

    public ReportTask(ReportGenerator reportGenerator, String category, String filename) {
        this.reportGenerator = reportGenerator;
        this.category = category;
        this.filename = filename;
    }

    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }

    public void setReportGenerator(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
