package javalang;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/6/3.
 */
public class TestLamdar1 {
    public static void main(String[] args) {
        Map<String, Object> m = new HashMap<>();
        m.put("one", true);
        m.put("two", false);
        System.out.println(BooleanUtils.toBoolean((Boolean)m.get("false")));
    }

    public void test1(int a, int b){
        System.out.println(a + b);
    }
}
