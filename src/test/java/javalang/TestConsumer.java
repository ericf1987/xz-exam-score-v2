package javalang;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author by fengye on 2017/1/11.
 */
public class TestConsumer {
    public static void main(String[] args) {
        TestConsumer tc = new TestConsumer();
        Consumer<String> c = ss -> System.out.println(ss.length());
        c.accept("hello");

        List<Student> list = Arrays.asList(
                tc.new Student("Jack", 29),
                tc.new Student("Mike", 50)
        );

        Consumer<Student> c1 = student -> student.setName(student.getName() + " James");
        Consumer<Student> c2 = student -> student.setGpa(student.getGpa() * 1.5);
        tc.doConsume(list, c1);
        tc.doConsume(list, c2);
        System.out.println(list.toString());
    }

    public List<Student> doConsume(List<Student> list, Consumer<Student> c){
        list.forEach(c::accept);
        return list;
    }

    class Student {
        private String name;
        private double gpa;

        public Student(String name, double gpa) {
            this.name = name;
            this.gpa = gpa;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getGpa() {
            return gpa;
        }

        public void setGpa(double gpa) {
            this.gpa = gpa;
        }

        @Override
        public String toString() {
            return this.getName() + ":" + this.getGpa();
        }
    }
}
