package com.xyoye.danmuxposed.utils;

import android.os.Environment;

/**
 * Created by xyy on 2018-03-21 下午 4:54
 */

public class DanmuConfig {
    public static final int FOLDER = 1;
    public static final int FILE = 0;
    public static final String DEFAULT_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DanmuXposed/";

    public static final String DANMU_FONT_SIZE_KEY = "danmu_font_size";
    public static final String DANMU_SPEED_KEY = "danmu_speed";
    public static final String MOBILE_DANMU_KEY = "mobile_danmu";
    public static final String TOP_DANMU_KEY = "top_danmu";
    public static final String BUTTON_DANMU_KEY = "button_danmu";
    public static final String READ_FILE_TYPE_KEY = "read_file_type";
    public static final String READ_FILE_PATH_KEY = "read_file_path";
    public static final String READ_FOLDER_PATH_KEY = "read_folder_path";
}
