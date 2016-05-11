package com.xz.taskdispatchers;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.taskdispatchers.impl.TotalScoreTaskDispatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
public class TotalScoreTaskDispatcherTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreTaskDispatcher dispatcher;

    @Test
    public void testDispatch() throws Exception {
        dispatcher.dispatch(PROJECT_ID);
    }
}