package javalang;

/**
 * @author by fengye on 2017/4/7.
 */
public class TestThreadState {

    public void testStateRunnable(){
        Thread thread = new Thread(() -> {
            for(int i = 0;i < Integer.MAX_VALUE;i++){
                System.out.println(i);
            }
        }, "RUNNABLE-Thread");

        thread.start();
    }

    public void testStateBlock() {
        final Object lock = new Object();

        Thread threadA = new Thread(() -> {
            inBlockSync(lock);
        }, "BLOCK-Thread-A");

        Thread threadB = new Thread(() -> {
            inBlockSync(lock);
        }, "BLOCK-Thread-B");

        threadA.start();
        threadB.start();
    }

    public void inBlockSync(Object lock) {
        synchronized (lock){
            System.out.println(Thread.currentThread().getName() + " invoke");
            try {
                Thread.sleep(200000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void testStateWaiting(){
        final Object lock = new Object();

        Thread threadA = new Thread(() -> {
            synchronized (lock){
                try {
                    lock.wait();
                    System.out.println("wait over");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "WAITING-Thread-A");

        Thread threadB = new Thread(() -> {
            synchronized (lock){
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "WAITING-Thread-B");

        threadA.start();
        threadB.start();
    }

    public static void main(String[] args) {
        /*Thread thread = new Thread();
        System.out.println(thread.getState());*/

    }
}
