package javalang;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2016/6/3.
 */
public class TestLamdar1 {
    public static void main(String[] args) {
        Map<String, Object> m1 = new HashMap<>();
        m1.put("k1", "123");
        m1.put("k2", 123);
        m1.put("k3", true);
        if(Boolean.parseBoolean(m1.get("k3").toString())){
            System.out.println(m1.get("k3").getClass());
            System.out.println(m1.get("k2").getClass());
            System.out.println(m1.get("k1").getClass());
            System.out.println(MapUtils.getBoolean(m1, "key4"));
            System.out.println(BooleanUtils.toBoolean((Boolean)m1.get("111")));
        }
    }

    public void test1(int a, int b){
        System.out.println(a + b);
    }
}
