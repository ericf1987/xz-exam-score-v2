package com.xz.taskdispatchers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.mqreceivers.AggrTask;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
@TaskDispatcherInfo(taskType = "average")
public class AverageTaskDispatcher extends TaskDispatcher {

    @Autowired
    MongoDatabase scoreDatabase;

    @Override
    public void dispatch(String projectId) {
        List<String> classIds = queryClassIds(projectId);

        for (String classId : classIds) {
            AggrTask task = createTask().setRange("class", classId);
            dispatchTask(task);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> queryClassIds(String projectId) {
        List<String> classIds = new ArrayList<>();

        FindIterable<Document> documents = scoreDatabase
                .getCollection("class_list")
                .find(new Document("projectId", projectId));

        documents.forEach((Consumer<Document>) document ->
                classIds.addAll((List<String>) document.get("classIds")));

        return classIds;
    }
}
