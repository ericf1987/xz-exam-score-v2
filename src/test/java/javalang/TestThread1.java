package javalang;

/**
 * @author by fengye on 2017/4/8.
 */
public class TestThread1 {

    class A extends Thread {
        private int threadNo;

        public A(int threadNo) {
            this.threadNo = threadNo;
        }

        @Override
        public void run() {
            synchronized (TestThread1.class) {
                for (int i = 0; i < 100; i++) {
                    System.out.println("thread" + threadNo + " " + i);
                }
            }
        }
    }

    class B extends Thread {
        private int threadNo;
        private String lock;

        public B(int threadNo, String lock) {
            this.threadNo = threadNo;
            this.lock = lock;
        }

        @Override
        public void run() {
            synchronized (this.getClass()){
                for (int i = 0; i < 100; i++) {
                    System.out.println("thread" + threadNo + " " + i);
                }
            }
            //abc();
        }

        public synchronized void abc(){
            for (int i = 0; i < 100; i++) {
                System.out.println("thread" + threadNo + " " + i);
            }
        }
    }

    public static void main(String[] args) {
        TestThread1 t1 = new TestThread1();
        /*for (int i = 1; i < 10; i++) {
            t1.new A(i).start();
        }*/
        String lock = new String("lock");
        for(int i = 0; i < 10; i++){
            t1.new B(i, lock).start();
        }
    }
}
