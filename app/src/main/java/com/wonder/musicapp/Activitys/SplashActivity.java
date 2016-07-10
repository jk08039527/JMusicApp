package com.wonder.musicapp.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wonder.musicapp.R;
import com.wonder.musicapp.Services.PlayService;

import java.lang.ref.WeakReference;

public class SplashActivity extends BaseActivity {
    private static final int START_ACTIVITY = 0x1;
    private Myhandler myhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
        myhandler = new Myhandler(this);
        myhandler.sendEmptyMessageDelayed(START_ACTIVITY, 3000);
    }

    private class Myhandler extends Handler {
        private WeakReference<Activity> reference;

        public Myhandler(Activity activity) {
            reference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (reference.get() != null) {
                switch (msg.what) {
                    case START_ACTIVITY:
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                        break;
                }
            }
        }
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int progress) {

    }

}
