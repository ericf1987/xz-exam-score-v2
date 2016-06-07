package javalang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/6/3.
 */
public class TestLamdar {
    public void test1() {
        List<String> strList = Arrays.asList("123", "456", "abc", null);
        String csv = strList.stream().
                filter(Objects::nonNull).
                collect(Collectors.joining(","));
        System.out.println(csv);
    }

    public static void main(String[] args) {
        new TestLamdar().test2();
    }

    public void test2(){
        List<String> strList = Arrays.asList("1111","22","3333","4444");
        strList.forEach(
                 a -> System.out.println(a)
        );

    }
}
