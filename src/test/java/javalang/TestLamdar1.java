package javalang;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;

import java.util.*;

/**
 * @author by fengye on 2016/6/3.
 */
public class TestLamdar1 {
    public static void main(String[] args) {
        Map<String, Object> m = new HashMap<>();
        m.put("ONE", 1);
        m.put("TWO", 1);
        m.put("THREE", 1);
        Set<String> set = m.keySet();
        System.out.println(set);
    }

    public void test1(int a, int b){
        System.out.println(a + b);
    }
}
