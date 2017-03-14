package com.xz.examscore.paperScreenShot.bean;

import java.io.File;
import java.util.List;

/**
 * @author by fengye on 2017/3/13.
 */
public class PaperScreenShotZipParam {
    private String projectId;

    private File outputFile;

    private List<String> srcEntryDir;

    private List<String> zipEntryDir;

    public String getProjectId() {

        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public List<String> getSrcEntryDir() {
        return srcEntryDir;
    }

    public void setSrcEntryDir(List<String> srcEntryDir) {
        this.srcEntryDir = srcEntryDir;
    }

    public List<String> getZipEntryDir() {
        return zipEntryDir;
    }

    public void setZipEntryDir(List<String> zipEntryDir) {
        this.zipEntryDir = zipEntryDir;
    }

    public PaperScreenShotZipParam(String projectId, File outputFile, List<String> srcEntryDir, List<String> zipEntryDir) {
        this.projectId = projectId;
        this.outputFile = outputFile;
        this.srcEntryDir = srcEntryDir;
        this.zipEntryDir = zipEntryDir;
    }

    @Override
    public String toString() {
        return "PaperScreenShotZipParam{" +
                "projectId='" + projectId + '\'' +
                ", outputFile=" + outputFile +
                ", srcEntryDir=" + srcEntryDir +
                ", zipEntryDir=" + zipEntryDir +
                '}';
    }
}
