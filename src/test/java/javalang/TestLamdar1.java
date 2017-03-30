package javalang;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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
        TestLamdar1 t = new TestLamdar1();
//        String imgPath = "F:/77094b36acaf2eddef675a92881001e939019332.jpg";
        String imgPath = "F:/sss.png";
//        String imgUrl = "http://pic.sc.chinaz.com/files/pic/pic9/201204/xpic4174.jpg";
//        String imgUrl = "http://znxunzhi-marking-pic.oss-cn-shenzhen.aliyuncs.com/430300-9cef9f2059ce4a36a40a7a60b07c7e00/21454f2c-c3c7-4dcc-8a62-eef2164c07d1/001/01/paperImage/310160034_positive.png";

        try {

            //画矩形
            BufferedImage bufferedImage = ImageIO.read(new File(imgPath));
//            BufferedImage bufferedImage = ImageIO.read(new URL(imgUrl));

            //转成黑白图片
//            toBlackAndWhite(bufferedImage);

            BufferedImage newImg = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            File rectOutputFile = new File("F:/rect2.png");
            t.fillRect(newImg, 100, 100, 50, 50);
            Font font = new Font("宋体", Font.PLAIN, 50);
            t.drawString(t.fillRect(newImg, 100, 100, 50, 50), "总", font, 100, 100 + font.getSize());
            t.saveImage(newImg, "jpg", rectOutputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage drawString(BufferedImage bufferedImage, String content, Font font, int x, int y) {
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.RED);
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

    public static void toBlackAndWhite(BufferedImage img){
        toBlackAndWhite(img, 50);
    }

    public static void toBlackAndWhite(BufferedImage img, int precision) {
        int w = img.getWidth();
        int h = img.getHeight();
        precision = (0 <= precision && precision <= 100) ? precision : 50;

        int limit = 255 * precision / 100;

        for(int i = 0, j; i < w; ++i){
            for(j = 0; j < h; ++j){
                Color color = new Color(img.getRGB(i, j));
                if(limit <= color.getRed() || limit <= color.getGreen() || limit <= color.getBlue()){
                    img.setRGB(i, j, Color.WHITE.getRGB());
                }
                else{
                    img.setRGB(i, j, Color.BLACK.getRGB());
                }
            }
        }
    }

}
