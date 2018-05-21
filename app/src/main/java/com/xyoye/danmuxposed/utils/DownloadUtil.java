package com.xyoye.danmuxposed.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Created by xyy on 2018/5/14.
 */

public class DownloadUtil {
    private static String BaseUrl = "http://comment.bilibili.tv/";

    public static String getXmlString(String cid){
        InputStream in = null;
        InputStream flin = null;
        Scanner sc = null;
        try {
            String xmlUrl = BaseUrl + cid + ".xml";
            URL url = new URL(xmlUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            conn.connect();

            in = conn.getInputStream();
            flin = new InflaterInputStream(in, new Inflater(true));

            sc = new Scanner(flin, "utf-8");

            StringBuilder stringBuffer = new StringBuilder();
            while(sc.hasNext())
                stringBuffer.append(sc.nextLine());
            return stringBuffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (sc != null)
                    sc.close();
                if (flin != null)
                    flin.close();
                if (in != null)
                    in.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        return null;
    }

    public static boolean isNum(String str){

        Pattern pattern = Pattern.compile("^-?[0-9]+");
        if(pattern.matcher(str).matches()){
            //数字
            return true;
        } else {
            //非数字
            return false;
        }
    }

    public static boolean isUrl(String str){
        String regex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$";
        Pattern pattern = Pattern.compile(regex);
        if(pattern.matcher(str).matches()){
            //数字
            return true;
        } else {
            //非数字
            return false;
        }
    }

}
