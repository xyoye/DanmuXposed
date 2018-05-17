package com.xyoye.danmuxposed.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
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

    public static void writeXmlFile(String xmlContent, String fileName , String path){
        FileOutputStream fos;
        BufferedWriter bw = null;
        try {
            String localPath = path + "/" + fileName+".xml";

            File folder = new File(path);
            if (!folder.exists()) {
                if (folder.mkdirs()){
                    System.out.println("成功创建文件夹");
                }
            }

            fos = new FileOutputStream(localPath, false);
            bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("utf-8")));
            bw.write(xmlContent);
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
