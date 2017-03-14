package com.xz.examscore.paperScreenShot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author by fengye on 2017/3/1.
 */
public class PaintUtils {

    static final Logger LOG = LoggerFactory.getLogger(PaintUtils.class);

    public static final String PNG = "png";
    public static final String JPG = "jpg";

    public static final String SCREEN_SHOT_SUFFIX_PNG = ".png";
    public static final String SCREEN_SHOT_SUFFIX_JPG = ".jpg";

    /**
     * 导入网络图片到缓冲区
     *
     * @param imgName URL地址
     * @return 图片缓存对象
     */
    public static BufferedImage loadImageUrl(String imgName) {
        try {
            URL url = new URL(imgName);
            return ImageIO.read(url);
        } catch (Exception e) {
            LOG.error("获取网络图片URL失败！URL:{}", imgName);
        }
        return null;
    }

    /**
     * 生成新图片到本地
     *
     * @param newImage 本地图片生成路径
     * @param img      图片缓存
     * @param suffix   扩展名
     */
    public static void writeImageLocal(String newImage, BufferedImage img, String suffix) {
        if (newImage != null && img != null) {
            File outputFile = new File(newImage);
            try {
                ImageIO.write(img, suffix, outputFile);
            } catch (IOException e) {
                LOG.error("试卷截图图片保存至本地失败！保存路径为：{}", newImage);
            }
        }
    }

    /**
     * 修改图片，返回修改后的图片缓冲区（单行文本）
     *
     * @param img       图片缓存对象
     * @param content   内容
     * @param font      字体
     * @param positionX 宽度坐标
     * @param positionY 高度坐标
     * @return 修改后的图片缓存对象
     */
    public static BufferedImage modifyImage(BufferedImage img, String content, Font font, float positionX, float positionY) {
        Graphics2D g;
        try {
            int w = img.getWidth();
            int h = img.getHeight();

            //起始位置
            float x;//宽度
            float y;//高度

            //计算画笔起始位置
            if (positionX > w || positionY > h) {
                /*x = w - font.getSize() * content.toString().length() + 2;
                y = h - font.getSize() + 2;*/
                LOG.info("试卷扫描截图出现异常，坐标位置大于图片识别区域！");
                return img;
            } else {
                x = positionX;//宽度
                y = positionY + font.getSize() + 2;//高度
            }


            g = img.createGraphics();
            g.setBackground(Color.WHITE);//背景颜色
            g.setColor(Color.BLACK);//字体颜色
            g.setFont(font);

            if (content != null) {
                g.drawString(new String(content.getBytes(), "UTF-8"), x, y);
            }
            g.dispose();
        } catch (Exception e) {
            LOG.error("修改图片失败！");
        }

        return img;
    }
}
