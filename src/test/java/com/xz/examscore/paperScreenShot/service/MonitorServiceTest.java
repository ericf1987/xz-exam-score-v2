package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.paperScreenShot.bean.TaskProcess;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author by fengye on 2017/3/26.
 */
public class MonitorServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    MonitorService monitorService;

    @Test
    public void testAddFinishCount() throws Exception {
        String projectId = "430900-9e8f3c054d72414b81cdd99bd48da695";
        monitorService.increaseFinished(projectId, TaskProcess.GENERATE_PAPER_SCREEN_SHOT);
    }

    @Test
    public void testGetTotalClass() throws Exception {
        int totalClass = monitorService.getTotal("430900-9e8f3c054d72414b81cdd99bd48da695");
        System.out.println(totalClass);
    }

    @Test
    public void testgetFinishedCount() throws Exception {
        String projectId = "430900-9e8f3c054d72414b81cdd99bd48da695";
        int finishedCount = monitorService.getFinishedCount(projectId, TaskProcess.GENERATE_PAPER_SCREEN_SHOT);
        System.out.println(finishedCount);
    }

    @Test
    public void testGetFinishedRate() throws Exception {
        String projectId = "430900-9e8f3c054d72414b81cdd99bd48da695";
        System.out.println(monitorService.getFinishRate(projectId, TaskProcess.GENERATE_PAPER_SCREEN_SHOT));
    }

    @Test
    public void testreset() throws Exception {
        String projectId = "430900-9e8f3c054d72414b81cdd99bd48da695";
        monitorService.reset(projectId, TaskProcess.GENERATE_PAPER_SCREEN_SHOT);
        System.out.println(monitorService.getFinishRate(projectId, TaskProcess.GENERATE_PAPER_SCREEN_SHOT));
    }

    /**
     * 测试记录生成截图失败的学生
     * @throws Exception
     */
    @Test
    public void testrecordFailedStudent() throws Exception {
        String projectId = "430300-f39dc20ba0044b8b9cb302d7910c87c4";
        String schoolId = "a315d03c-e989-4da0-a950-35fd51bc396f";
        String classId = "f9085346-e73d-462a-aabb-bd77c444cd87";
        String subjectId = "001";
        List<String> studentIds = Arrays.asList("111", "222", "333");
//        monitorService.createFailedStudentDoc(projectId, schoolId, classId, Arrays.asList("001", "002"));
        monitorService.recordFailedStudent(projectId, schoolId, classId, studentIds, subjectId);
    }
}