package com.xyoye.danmuxposed.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by xyy on 2018-03-23 下午 5:18
 */

public class ToastUtil {

    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}
