package javalang;

/**
 * @author by fengye on 2016/12/7.
 */
public class TestThreadLocal {

    private static ThreadLocal<Integer> counter = new ThreadLocal() {
        public Integer initialValue() {
            return 0;
        }
    };
    int num = 0;
    public int getNextNum(){
        return num++;
    }

    public int getNextCount() {
        counter.set(counter.get() + 1);
        return counter.get();
    }

    public static void main(String[] args) {
        TestThreadLocal local = new TestThreadLocal();
        TestNum tn1 = local.new TestNum(local);
        TestNum tn2 = local.new TestNum(local);
        TestNum tn3 = local.new TestNum(local);
        tn1.start();
        tn2.start();
        tn3.start();
    }

    private class TestNum extends Thread {

        private TestThreadLocal local;

        public TestNum(TestThreadLocal local) {
            this.local = local;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                System.out.println("thread[" + Thread.currentThread().getName() + "] --> local_num["
                        + local.getNextNum() + "]");
                System.out.println("thread[" + Thread.currentThread().getName() + "] --> local_counter["
                        + local.getNextCount() + "]");
            }
        }
    }
}
