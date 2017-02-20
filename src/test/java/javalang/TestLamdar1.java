package javalang;

import com.xz.examscore.util.ChineseName;
import org.apache.commons.lang.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author by fengye on 2016/6/3.
 */
public class TestLamdar1 {
    class Counter {
        int count = 0;

        public void add(String name) {
            synchronized (this) {
                System.out.println("开始执行add，线程名称为" + name + ", count=" + count);
                count++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("执行完成add，线程名称为" + name + ", count=" + count);
                }
            }
        }

        public void sub(String name) {
            synchronized (this) {
                System.out.println("开始执行sub，线程名称为" + name + ", count=" + count);
                count--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("执行完成sub，线程名称为" + name + ", count=" + count);
                }
            }
        }
    }

    class MyThread extends Thread {

        Counter counter;

        public MyThread(Counter counter) {
            this.counter = counter;
        }

        public void run() {
            counter.add(this.getName());
            counter.sub(this.getName());
        }
    }

    public static void main(String[] args) {
        List<String> s = Arrays.asList("111", "123123", "123125", "aaaa", "sdasdf");
        System.out.println(s.subList(0, 100).toString());
    }

}
