package com.xz.mqreceivers.impl;

import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ReceiverInfo(taskType = "score_segment")
public class ScoreSegmentTask extends Receiver {

    @Autowired
    StudentService studentService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        List<String> studentIds = studentService.getStudentList(projectId, range, target);
    }
}
