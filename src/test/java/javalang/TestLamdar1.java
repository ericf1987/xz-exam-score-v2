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
/*        File file = new File("F:\\paper\\screenshot\\paperScreenShot-zip\\湘潭市高一联考\\湘乡市第二中学\\所有班级\\1601_试卷截图.zip");
        System.out.println(file.getPath());
        System.out.println(file.getAbsoluteFile());
        System.out.println(file.getName().split("_")[0]);*/
        long l = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        System.out.println(l);
        System.out.println(calendar.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format.format(calendar.getTime());
        System.out.println(date);
        try {
            Date parse = format.parse(date);
            System.out.println(parse.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String fileName = "湘乡市第二中学_所选班级_试卷截图_1489111085468.zip";
        String s = fileName.split("\\.")[0];
        System.out.println(s);
        String[] split = s.split("_");
        System.out.println(split[split.length - 1]);

        System.out.println("============");
        long now = System.currentTimeMillis();
        System.out.println(now - 3600000);
    }



}
