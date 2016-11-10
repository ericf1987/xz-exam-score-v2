package json;

import com.alibaba.fastjson.JSON;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * (description)
 * created at 16/11/10
 *
 * @author yiding_he
 */
public class JSONTest {

    public static void main(String[] args) {
        String message = "{\"aggregationId\":\"b70cf6ea-757f-4907-9371-11780e14b5d4\"," +
                "\"projectId\":\"FAKE_PROJ_1478750870617_0\"," +
                "\"target\":{\"id\":\"FAKE_PROJ_1478750870617_0:001:3\",\"name\":\"quest\"}," +
                "\"type\":\"total_score\"}";

        Class<AggrTaskMessage> beanType = AggrTaskMessage.class;

        AggrTaskMessage taskMessage = JSON.parseObject(message, beanType);
        System.out.println(taskMessage.getRange());
        System.out.println(taskMessage.getTarget());
    }
}
