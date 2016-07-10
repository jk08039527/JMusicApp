package com.wonder.musicapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;

/**
 * Created by Administrator on 2016/2/14.
 */
public class JerryPlayerApplication extends Application {
    public static SharedPreferences sp;
    public static DbUtils dbUtils;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE);
        dbUtils = DbUtils.create(getApplicationContext(), Constant.DB_NAME);
        context=getApplicationContext();
    }
}
