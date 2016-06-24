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


    interface Converter<F, T> {
        T convert(F from);
    }

    class Person{
        private String name;
        private int num;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public Person() {
        }

        public Person(String name, int num) {
            this.name = name;
            this.num = num;
        }
    }

    interface PersonFactory<P extends Person>{
        P createPerson(String name, int num);
    }


    public static void main(String[] args) {
        //new TestLamdar().test2();
        Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
        Integer integer = converter.convert("123");
        System.out.println(integer.getClass());
    }

    public void test2() {
        List<String> strList = Arrays.asList("1111", "22", "3333", "4444");
        strList.forEach(
                a -> System.out.println(a)
        );

    }
}
