package javalang;

import java.util.*;
import java.util.regex.Pattern;
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

    class Person {
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

    interface PersonFactory<P extends Person> {
        P createPerson(String name, int num);
    }


    public static void main(String[] args) {
        //new TestLamdar().test3();
        Map<String, String> map1 = new HashMap<>();
        map1.put("name", "冯也");
        map1.put("sex", "fale");
        Map<String, String> map2 = new HashMap<>();
        map2.put("name", "冯也2");
        map2.put("sex", "fale");
        List<String> l1 = new ArrayList<>();
        l1.addAll(map1.keySet());
        l1.addAll(map2.keySet());
        l1.forEach(System.out::println);

//        Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
//        Converter<String, Integer> converter = Integer::valueOf;
//        Integer integer = converter.convert("1980");
//        System.out.println(integer.getClass());
    }

    public void test2() {
        List<String> strList = Arrays.asList("1111", "22", "3333", "4444");
        strList.forEach(
                a -> System.out.println(a)
        );

    }

    public void test3() {
        List<String> subjectIds = Arrays.asList("AAAAAA", "AAAAAB", "AAAABC", "BAAACA", "AAACCC", "BBBBAC", "AAAAC");
        //subjectIds.sort(String::compareTo);
        subjectIds.sort((String s1, String s2) -> s1.compareTo(s2));
        System.out.println(subjectIds.toString());
    }

    public void test4(){
        Pattern like = Pattern.compile("^" + "2016");
        System.out.println(like);
    }
}
