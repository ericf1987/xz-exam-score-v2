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
        List<Double> l = new ArrayList<>();
        l.add(0.2d);
        l.add(0.3d);
        l.add(0.1d);
        System.out.println(l.get(l.size() - 1));
    }

    public void test1(int a, int b){
        System.out.println(a + b);
    }
}
