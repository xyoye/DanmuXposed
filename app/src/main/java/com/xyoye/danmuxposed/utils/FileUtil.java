package com.xyoye.danmuxposed.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xyy on 2018-03-24 下午 5:03
 */


public class FileUtil {

    public static String getFileName(String url){
        try {
            String[] file_name_array = url.split("/");
            String file_name = file_name_array[file_name_array.length-1];
            int Suffix = file_name.lastIndexOf(".");
            file_name = file_name.substring(0,Suffix);
            return file_name;
        }catch (Exception e){
            e.printStackTrace();
            return  url;
        }
    }

    public static List<String> getAllFile(String directoryPath, boolean isAddDirectory) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }
}
