package javalang;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author by fengye on 2017/1/11.
 */
public class TestStream {
    public static void main(String[] args) {
        TestStream ts = new TestStream();

        List<String> list = Arrays.asList(
                "hello", "jack", "talkweb", "stream", "zhongnanxunzhi", "ccc", "aaa"
        );


        list.stream().filter(s -> !s.startsWith("c"))
                .map(String::length)
                .sorted().collect(Collectors.toList()).forEach(System.out::println);

        list.stream().findFirst().map(s -> s + "world").ifPresent(System.out::println);
        list.stream().findAny().map(s -> s + "world").ifPresent(System.out::println);

        Arrays.stream(new int[]{1, 2, 3, 5, 6, 19}).map(a -> a * 2 - 1)
                .average().ifPresent(System.out::println);

        Stream.of("a1", "b1", "c1").map(s -> s.substring(1)).mapToInt(Integer::parseInt).
                max().ifPresent(System.out::println);

        Stream.of(123, 33123, 123, 9980, 809871).distinct().collect(Collectors.toList())
                .forEach(System.out::println);


        IntStream.range(1, 10).mapToObj(i -> 'a' + i).forEach(System.out::println);
        Stream.of('a', 'b', 'c', '^', '%').mapToInt(i -> 100 + i).forEach(System.out::println);

        List<Person> persons = Arrays.asList(
                ts.new Person("jk", 20),
                ts.new Person("pss", 20),
                ts.new Person("uu", 21),
                ts.new Person("ii", 22),
                ts.new Person("pp", 30),
                ts.new Person("cc", 30),
                ts.new Person("zz", 33)
        );

        Map<Integer, List<Person>> personMap = persons.stream().collect(Collectors.groupingBy(Person::getAge));
        personMap.forEach((age, p) -> System.out.format("age %s %s\n", age, p));
        Double aDouble = persons.stream().collect(Collectors.averagingDouble(Person::getAge));
        System.out.println(aDouble);

        DoubleSummaryStatistics collect = persons.stream().collect(Collectors.summarizingDouble(Person::getAge));
        System.out.println(collect);

        //难点，将对象中的age作为key， name作为value，同一个key的多个value用;分隔开
        Map<Integer, String> collect1 = persons.stream().collect(Collectors.toMap(
                p -> p.age,
                p -> p.name,
                (name1, name2) -> name1 + ";" + name2
        ));

        System.out.println(collect1);

        List<Foo> foos = new ArrayList<>();

        IntStream.range(1, 4).forEach(i -> foos.add(ts.new Foo("foo" + i)));

        foos.forEach(
                f -> IntStream.range(1, 4).forEach(
                        i -> f.bars.add(ts.new Bar("bar" + i + "<-" + f.name))
                )
        );

        //将一个对象的stream转化为多个对象的stream
        foos.stream().flatMap(f -> f.bars.stream())
                .forEach(b -> System.out.println(b.name));

        Arrays.asList("A1", "B1", "C1", "D1", "E1")
                .parallelStream()
                .filter(a -> {
                    System.out.format("filter %s : [%s]\n", a, Thread.currentThread().getName());
                    return true;
                })
                .map(a -> {
                    System.out.format("map %s : [%s]\n", a, Thread.currentThread().getName());
                    return a.toLowerCase();
                })
                .forEach(a -> System.out.format("forEach %s : [%s]\n", a, Thread.currentThread().getName()));

        persons.stream().reduce((p1, p2) -> p1.getAge() > p2.getAge() ? p1 : p2).ifPresent(p -> System.out.println(p.getAge()));

        Person person = persons.stream().reduce(ts.new Person("", 0), (p1, p2) -> {
            p1.age += p2.age;
            p1.name += p2.name;
            return p1;
        });

        System.out.format("name=%s, age=%s", person.getName(), person.getAge());

        Formula formula = new Formula() {
            @Override
            public double calculate(double d) {
                return test(d) + d;
            }
        };

        System.out.println(formula.calculate(100));
        System.out.println(formula.test(100));

        list.stream().reduce((s1, s2) -> s1 + "$$$" + s2).ifPresent(System.out::println);
    }

    interface Formula{
        double calculate(double d);

        default double test(double d){
            return Math.sqrt(d);
        }
    }

    class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    class Foo {
        String name;
        List<Bar> bars = new ArrayList<>();

        public Foo(String name) {
            this.name = name;
        }
    }

    class Bar {
        String name;

        public Bar(String name) {
            this.name = name;
        }
    }
}
