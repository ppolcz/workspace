package com.polpe.panogl.util;

import android.os.Environment;

public class GlobalConstants {

    public final static float DEFAULT_FOV_DEG = 60.0f;
    public final static int MAX_PANO_IMAGE_WIDTH = 6000;
    public final static int DEFAULT_MAX_TEXTURE_SIZE = 1024;

    public static final float NULL = 0.0f;
    public static final float TO_DEGF = (float) (180.0 / Math.PI);
    public static final float TO_RADF = (float) (Math.PI / 180.0);
    public static final double TO_DEGD = 180.0 / Math.PI;
    public static final double TO_RADD = Math.PI / 180.0;
    
    public static final String DIR_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.panogl";
    public static final String DIR_ROOT_PANO_PREFIX = DIR_ROOT + "/panorama";
    
    public static final String FILE_IMAGE_PREFIX = "image";
    public static final String FILE_IMAGE_EXTENSION = "png";
    public static final String FILE_TEXT_EXTENSION = "txt";
    
    public static final String FILENAME_PANO = "panorama.jpg";
    public static final String FILENAME_LEFT = "pano_left.jpg";
    public static final String FILENAME_RIGHT = "pano_right.jpg";
    public static final String FILENAME_BOTTOM = "pano_bottom.jpg";
    public static final String FILENAME_TOP = "pano_top.jpg";
    public static final String FILENAME_FRONT = "pano_front.jpg";
    public static final String FILENAME_BACK = "pano_back.jpg";

    public static final String KEY_PANORAMA_NAME = "KEY_PANORAMA_NAME";
}
