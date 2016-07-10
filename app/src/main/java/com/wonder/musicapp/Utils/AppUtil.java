package com.wonder.musicapp.Utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.wonder.musicapp.JerryPlayerApplication;

/**
 * Created by Administrator on 2016/2/27.
 */
public class AppUtil {
    //隐藏输入法
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) JerryPlayerApplication
                .context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
