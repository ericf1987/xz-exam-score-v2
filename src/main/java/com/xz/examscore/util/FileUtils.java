package com.xz.examscore.util;

import java.io.File;

/**
 * @author by fengye on 2016/7/6.
 */
public class FileUtils {
    // 验证字符串是否为正确路径名的正则表达式
    private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";

    public static boolean deleteFolder(String sPath){
        return true;
    }

    public static boolean deleteFile(String sPath){
        boolean flag = false;
        File file = new File(sPath);
        if(file.isFile() && file.exists()){
            file.delete();
            flag = true;
        }
        return flag;
    }
}
