package com.wonder.musicapp;

/**
 * Created by Administrator on 2016/2/14.
 */
public class Constant {
    public static final String SP_NAME = "JerryMusic";
    public static final String DB_NAME = "dbMusic.db";
    public static final int PLAY_RECORD_NUM = 5;//最近播放显示的最大数

    //百度音乐的地址
    public static final String BAIDU_URL = "http://music.baidu.com/";
    //热歌榜
    public static final String BAIDU_DAYHOT = "top/dayhot/?pst=shouyeTop";
    //搜歌
    public static final String BAIDU_SEARCH = "search/song";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36";

    //成功标记
    public static final int SUCCESS = 1;
    //失败标记
    public static final int FAILED = 2;

    public static final String DIR_MUSIC = "/JerryMusic/music";
    public static final String DIR_PIC = "/JerryMusic/pic";
    public static final String DIR_LRC = "/JerryMusic/lrc";

    public static final int TIMEOUT = 6 * 1000;
}
