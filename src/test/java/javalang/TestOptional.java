package javalang;

import java.util.Optional;

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
        TestOptional to = new TestOptional();
        C c = to.new C();
        c.setName("foo");
        B b = to.new B();
        b.setC(c);
        A a = to.new A();
        a.setB(b);
        Optional.of(to.new A()).flatMap(aa -> Optional.ofNullable(aa.b))
                .flatMap(bb -> Optional.ofNullable(bb.c))
                .flatMap(cc -> Optional.ofNullable(cc.name));
    }
}
