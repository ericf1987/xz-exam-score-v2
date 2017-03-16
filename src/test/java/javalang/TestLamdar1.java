package javalang;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
/*        Runtime runtime = Runtime.getRuntime();
        System.out.println(runtime.availableProcessors());
        System.out.println(runtime.freeMemory());*/
        TestLamdar1 t = new TestLamdar1();
        String imgPath = "F:/77094b36acaf2eddef675a92881001e939019332.jpg";
        try {

            //画矩形
            BufferedImage bufferedImage = ImageIO.read(new File(imgPath));
            File rectOutputFile = new File("F:/rect1.jpg");
            t.fillRect(bufferedImage, 100, 100, 50, 50);
            Font font = new Font("宋体", Font.PLAIN, 50);
            t.drawString(t.fillRect(bufferedImage, 100, 100, 50, 50), "总", font, 100, 100 + font.getSize());
            t.saveImage(bufferedImage, "jpg", rectOutputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage drawString(BufferedImage bufferedImage, String content, Font font, int x, int y) {
        Graphics2D g = bufferedImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.setColor(Color.YELLOW);
        g.setFont(font);
        g.drawString(content, x, y);
        g.dispose();
        return bufferedImage;
    }


    //画矩形框图
    public BufferedImage drawRect(BufferedImage bufferedImage, int x, int y, int width, int height) {
        Graphics2D g = bufferedImage.createGraphics();
        g.drawRect(x, y, width, height);
        g.dispose();
        return bufferedImage;
    }

    //填充矩形图层
    public BufferedImage fillRect(BufferedImage bufferedImage, int x, int y, int width, int height) {
        Graphics2D g = bufferedImage.createGraphics();
        g.fillRect(x, y, width, height);
        g.dispose();
        return bufferedImage;
    }

    //保存切图
    public void getRectSubImage(BufferedImage bufferedImage, String suffix, File outputFile, int x, int y, int width, int height) {
        BufferedImage subImage = bufferedImage.getSubimage(x, y, width, height);
        try {
            ImageIO.write(subImage, suffix, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //保存图片
    public void saveImage(BufferedImage bufferedImage, String suffix, File outputFile) {
        try {
            ImageIO.write(bufferedImage, suffix, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
