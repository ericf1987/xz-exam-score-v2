package javalang;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/6/3.
 */
public class TestLamdar1 {
    public static void main(String[] args) {
        String s = "A";
        String t = "A,B,C";
        List<String> list = Arrays.asList(t.split(","));
        System.out.println(list.toString());
    }

    public void test1(int a, int b){
        System.out.println(a + b);
    }
}
