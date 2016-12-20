package com.xz.examscore.util;

import com.xz.ajiaedu.common.lang.StringUtil;

import java.util.regex.Pattern;

/**
 * @author by fengye on 2016/8/26.
 */
public class RankLevelFormater {

    //格式化等第参数 例如将4A1B1C转化成AAAABC
    public static String format(String str) {

        if (StringUtil.isEmpty(str)) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        //匹配数字
        Pattern p_number = Pattern.compile("\\d+");

        //匹配字母
        Pattern p_char = Pattern.compile("[a-zA-Z]+");
        //["4", "1", "1"]
        String[] numbers = p_char.split(str);
        //["", "A", "B", "C"]
        String[] chars = p_number.split(str);

        if (numbers.length == 0) {
            return str;
        } else {
            for (int i = 0; i < numbers.length; i++) {
                int pos = Integer.parseInt(numbers[i]);
                for (int j = 0; j < pos; j++)
                    builder.append(chars[i + 1]);
            }
            return builder.toString();
        }
    }

    //格式化等第参数 例如将AAAABC转化成4A1B1C
    public static String format2(String str){
        if (StringUtil.isEmpty(str)) {
            return "";
        }

        //数组存放对应26个字母的出现次数比如a[0]的值对应字母A出现的次数，a[2]的值对应C出现的次数。。。
        int[] arr = new int[26];

        for(int i = 0; i < str.length();i++){
            char c = str.charAt(i);
            int index = c - 'A';
            arr[index] = arr[index] + 1;
        }

        StringBuilder builder = new StringBuilder();
        for(int j = 0; j < arr.length; j++){
            if(arr[j] != 0){
                builder.append(arr[j]).append("").append((char) (j + 'A'));
            }
        }
        return builder.toString();
    }
}
