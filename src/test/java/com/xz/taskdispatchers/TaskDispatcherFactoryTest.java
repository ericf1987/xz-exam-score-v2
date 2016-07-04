package com.xz.taskdispatchers;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.AggregationType;
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
        assertFalse(dispatchers.isEmpty());
    }
}