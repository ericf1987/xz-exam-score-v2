package javalang;

import com.hyd.simplecache.utils.MD5;

import java.util.*;
import java.util.function.Predicate;
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

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public Person() {
        }

        public Person(String name, int num, int age) {
            this.name = name;
            this.num = num;
            this.age = age;
        }

        public void isMature(Person person, Predicate<Person> p){
            if(p.test(person)){
                System.out.println(person.getName() + "is Mature at the age of" + person.getAge());
            }else{
                System.out.println(person.getName() + "is not Mature at the age of" + person.getAge());
            }
        }

        public void isMultiple(Person person, Predicate<Person> p){
            if(p.test(person)){
                System.out.println(person.getName() + "is Multiple");
            }
        }
    }

    interface PersonFactory<P extends Person> {
        P createPerson(String name, int num);
    }


    public static void main(String[] args) {
/*        Predicate<Person> p1 = p -> p.getAge() >= 16;
        Predicate<Person> p2 = p -> p.getName().length() > 2;
        TestLamdar t = new TestLamdar();
        Person person1 = t.new Person("冯也", 1, 28);
        Person person2 = t.new Person("冯也111", 2, 10);
        person1.isMature(person1, p1);
        person1.isMature(person2, p1);*/
        System.out.println(Boolean.parseBoolean("1"));
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
