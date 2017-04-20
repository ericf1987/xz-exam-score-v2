package javalang;

import com.xz.ajiaedu.common.lang.CounterMap;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
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

    class Person {
        private String name;
        private int weight;
        private int age;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public Person() {
        }

        public Person(String name, int weight, int age) {
            this.name = name;
            this.weight = weight;
            this.age = age;
        }

    }

    public void isMature(Person person, Predicate<Person> p) {
        if (p.test(person)) {
            System.out.println(person.getName() + "is Mature at the age of" + person.getAge());
        } else {
            System.out.println(person.getName() + "is not Mature at the age of" + person.getAge());
        }
    }

    public void isMultiple(Person person, Predicate<Person> p) {
        if (p.test(person)) {
            System.out.println(person.getName() + "is Fat");
        }
    }

    public static void main(String[] args) {
/*        Integer i1 = 20;
        Integer i2 = 20;
        System.out.println(i1 == i2);

        Integer i3 = 200;
        Integer i4 = 200;
        System.out.println(i3 == i4);*/

        int i = 2;
        int j = 3;
        System.out.println(i | j);
    }

}
