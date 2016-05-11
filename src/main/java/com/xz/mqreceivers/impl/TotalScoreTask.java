package com.xz.mqreceivers.impl;

import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
@ReceiverInfo(taskType = "total_score")
public class TotalScoreTask extends Receiver {

    @Override
    public void taskReceived(AggrTask aggrTask) {
        Document query = new Document("projectId", aggrTask.getProjectId());

        String targetName = aggrTask.getTarget().getName();
        String targetQueryName = targetName.equals(Target.PROJECT) ? null :
                (targetName.equals(Target.SUBJECT) ? "subjectId" :
                        (targetName.equals(Target.QUEST) ? "questNo" : null));

        if (targetQueryName != null) {
            query.append(targetQueryName, aggrTask.getTarget().getId());
        }
    }
}
