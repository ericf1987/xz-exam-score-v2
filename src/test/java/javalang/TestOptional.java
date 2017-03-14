package javalang;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author by fengye on 2017/1/12.
 */
public class TestOptional {
    class A {
        B b;

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    class B {
        C c;

        public C getC() {
            return c;
        }

        public void setC(C c) {
            this.c = c;
        }
    }

    class C {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
/*        TestOptional to = new TestOptional();
        C c1 = to.new C();
        C c2 = to.new C();
        c1.setName("foo-c1");
        c2.setName("foo-c2");
        B b1 = to.new B();
        B b2 = to.new B();
        b1.setC(c1);
        A a = to.new A();
        Optional<A> o = Optional.of(a);
        System.out.println(o.isPresent());
        System.out.println(o.get().getB());

        Optional.of(a).map(aa -> Optional.ofNullable(aa.getB()));

        Optional.of(a).flatMap(aa -> Optional.ofNullable(aa.b))
                .flatMap(bb -> Optional.ofNullable(bb.c))
                .flatMap(cc -> Optional.ofNullable(cc.name));*/
        Stream<String> names = Stream.of("Luther", "Kobe", "Jim", "Kyo", "Nym", "Kite", "Many");
        Optional<String> second = names.filter(name -> name.length() == 4).findAny();
        System.out.println(second.orElse("This is No One!"));
    }
}
