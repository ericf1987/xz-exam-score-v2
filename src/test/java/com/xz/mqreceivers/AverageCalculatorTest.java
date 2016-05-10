package com.xz.mqreceivers;

import com.alibaba.fastjson.JSON;
import com.xz.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
public class AverageCalculatorTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;

    @Test
    public void testHandleCommand() throws Exception {
        AggrTask aggrTask = new AggrTask();
        aggrTask.setType("average");

        jedisConnectionFactory.getConnection().lPush(
                "aggregation_command_list".getBytes(), JSON.toJSONString(aggrTask).getBytes());
    }
}