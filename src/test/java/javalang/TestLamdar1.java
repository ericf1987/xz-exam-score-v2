package javalang;

import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.util.ChineseName;
import org.apache.commons.lang.BooleanUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author by fengye on 2016/6/3.
 */
public class TestLamdar1 {

    private static String string;

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
        Runtime runtime = Runtime.getRuntime();
        System.out.println(runtime.availableProcessors());
        System.out.println(runtime.freeMemory());
    }



}
