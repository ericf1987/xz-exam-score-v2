package com.xz.examscore.asynccomponents.aggrtaskdispatcher;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.AggregationType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class TaskDispatcherFactoryTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TaskDispatcherFactory taskDispatcherFactory;

    @Test
    public void testListAvailableDispatchers() throws Exception {
        List<TaskDispatcher> dispatchers = taskDispatcherFactory.listAvailableDispatchers("AGGR1", AggregationType.All);
        dispatchers.forEach(dispatcher -> System.out.println(dispatcher.getClass().getSimpleName()));
    }

    @Test
    public void testgetTaskDispatcher() throws Exception {
        TaskDispatcher total_score = taskDispatcherFactory.getTaskDispatcher("total_score");
        System.out.println(total_score.getClass().getSimpleName());
    }
}