package com.orangecoder.androidffmpegcmdtool.config;

import java.io.File;

/**
 * Created by xpmbp on 15/12/14.
 */
public class FilePathConfig {
    //sdcard缓存根目录
    public static final String PATH_BASE = "orangecoder";
    //Imagedownloader缓存路径
    public static final String PATH_IMAGEDOWNLOADER = PATH_BASE +
            File.separator + "ImageDownloadCache";
    //下载视频缓存路径
    public static final String PATH_DOWNLOADVIDEO = PATH_BASE +
            File.separator + "downloadvideo";
    //本地视频缓存路径
    public static final String PATH_LOCALVIDEO = PATH_BASE +
            File.separator + "localvideo";
    //本地视频缩略图缓存路径
    public static final String PATH_LOCALVIDEO_THUMBNAIL = PATH_BASE +
            File.separator + "localvideo_thumbnail";



}
