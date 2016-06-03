package com.xz.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * double 工具类
 *
 * @author zhaorenwu
 */
public class DoubleUtils {

    private static DecimalFormat FORMAT = new DecimalFormat("0.00");

    public static String toPercent(double value) {
        if (!isValidNumber(value)) {
            return "0%";
        }

        return FORMAT.format(value * 100) + "%";
    }

    /**
     * double 保留两位小数
     *
     * @param f 原数值
     *
     * @return 保留两位小数的数值
     */
    public static Double round(double f) {
        return round(f, false);
    }

    /**
     * double 保留两位或四位小数
     *
     * @param doubleValue       原数值
     * @param percent           是否是百分比，如果为 true 则返回四位小数
     *
     * @return 保留X位小数的数值
     */
    public static Double round(double doubleValue, boolean percent) {
        BigDecimal b = new BigDecimal(doubleValue);
        return b.setScale(percent ? 4 : 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static boolean isValidNumber(double d) {
        return !(
                d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY || Double.isNaN(d)
        );
    }
}
