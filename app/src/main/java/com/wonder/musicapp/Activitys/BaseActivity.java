package com.wonder.musicapp.Activitys;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.wonder.musicapp.JerryPlayerApplication;
import com.wonder.musicapp.Services.PlayService;

public abstract class BaseActivity extends AppCompatActivity {

    public PlayService playService;
    private boolean isBind = false;
    public static JerryPlayerApplication app;
    private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener() {
        @Override

        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };

    public abstract void publish(int progress);

    public abstract void change(int position);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (JerryPlayerApplication) getApplication();
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder) iBinder;
            playService = playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playService = null;
            isBind = false;
        }
    };

    //绑定服务
    public void bindPlayService() {
        if (!isBind) {
            Intent intent = new Intent(this, PlayService.class);
            bindService(intent, conn, BIND_AUTO_CREATE);
            isBind = true;
        }
    }

    //解绑服务
    public void unbindPlayService() {
        if (isBind) {
            unbindService(conn);
            isBind = false;
        }
    }
}
