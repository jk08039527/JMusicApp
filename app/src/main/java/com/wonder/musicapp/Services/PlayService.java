package com.wonder.musicapp.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.wonder.musicapp.Entitys.Mp3Info;
import com.wonder.musicapp.JerryPlayerApplication;
import com.wonder.musicapp.R;
import com.wonder.musicapp.Utils.MediaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音乐播放的服务组件
 * 实现的功能
 * 1、播放
 * 2、暂停
 * 3、上一首
 * 4、下一首
 * 5、获取当前的播放进度
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {
    private MediaPlayer mp;
    private int currentPosition = 0;//当前正在播放的位置
    private ArrayList<Mp3Info> mp3Infos;
    private MusicUpdateListener musicUpdateListener;
    //播放模式
    public static final int ORDER_PLAY = 1;
    public static final int RANDOM_PLAY = 2;
    public static final int SINGLE_PLAY = 3;
    private int play_mode = ORDER_PLAY;
    private Random random = new Random();
    private boolean isPause = false;
    //切换播放列表
    public static final int MY_MUSIC_LIST = 1;
    public static final int LIKE_MUSIC_LIST = 2;
    public static final int RECENT_MUSIC_LIST = 3;
    public static int changePlayList = MY_MUSIC_LIST;
    private ExecutorService es = Executors.newSingleThreadExecutor();

    public PlayService() {
    }

    public ArrayList<Mp3Info> getMp3Infos() {
        return mp3Infos;
    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JerryPlayerApplication app = (JerryPlayerApplication) getApplication();
        currentPosition = app.sp.getInt("currentPosition", 0);
        play_mode = app.sp.getInt("play_mode", 1);
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        mp3Infos = MediaUtil.getMp3Infos(this);
        es.execute(updateStatusRunnable);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es != null && !es.isShutdown()) {
            es.shutdown();
            es = null;
        }
        mp = null;
        mp3Infos = null;
        musicUpdateListener = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (musicUpdateListener != null && mp != null && mp.isPlaying()) {
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public boolean isPause() {
        return isPause;
    }

    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }

    public static int getChangePlayList() {
        return changePlayList;
    }

    public static void setChangePlayList(int changePlayList) {
        PlayService.changePlayList = changePlayList;
    }

    public int getPlayMode() {
        return play_mode;
    }

    /**
     * int ORDER_PLAY = 1;
     * int RANDOM_PLAY = 2;
     * int SINGLE_PLAY = 3;
     *
     * @return
     */
    public void setPlayMode(int play_mode) {
        this.play_mode = play_mode;
    }

    //播放
    public void play(int position) {
        if (position < 0 && position >= mp3Infos.size()) {
            position = 0;
        }
        Mp3Info mp3Info = mp3Infos.get(position);
        try {
            mp.reset();
            mp.setDataSource(mp3Info.getUrl());
            mp.prepare();
            mp.start();
            currentPosition = position;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (musicUpdateListener != null) {
            musicUpdateListener.onChange(currentPosition);
        }

    }

    //判断是否在播放
    public boolean isPlaying() {
        if (mp != null) {
            return mp.isPlaying();
        }
        return false;
    }

    //暂停
    public void pause() {
        if (mp.isPlaying()) {
            mp.pause();
        }
    }

    //上一首
    public void prev(int position) {
        if (mp3Infos==null||mp3Infos.size()==0){
            Toast.makeText(this, getString(R.string.noMusic), Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentPosition == 0) {
            currentPosition = mp3Infos.size() - 1;
        } else {
            currentPosition--;
        }
        play(currentPosition);
    }

    //下一首
    public void next() {
        if (mp3Infos==null||mp3Infos.size()==0){
            Toast.makeText(this, getString(R.string.noMusic), Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentPosition == mp3Infos.size() - 1) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        play(currentPosition);
    }

    //
    public void start() {
        if (!mp.isPlaying() && mp != null) {
            mp.start();
        }
    }

    public int getDuration() {
        return mp.getDuration();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void seekTo(int msec) {
        mp.seekTo(msec);
    }

    public int getCurrentProgress() {
        if (mp != null && mp.isPlaying()) {
            return mp.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (play_mode) {
            case ORDER_PLAY:
                next();
                break;
            case RANDOM_PLAY:
                play(random.nextInt(mp3Infos.size()));
                break;
            case SINGLE_PLAY:
                play(currentPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mp.reset();
        return false;
    }

    public class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }

    //更新状态的接口
    public interface MusicUpdateListener {
        public void onPublish(int progress);//修改进度

        public void onChange(int position);//更新位置
    }
}
