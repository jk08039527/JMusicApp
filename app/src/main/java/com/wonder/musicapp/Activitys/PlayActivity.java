package com.wonder.musicapp.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.wonder.musicapp.Entitys.Mp3Info;
import com.wonder.musicapp.R;
import com.wonder.musicapp.Services.PlayService;
import com.wonder.musicapp.Utils.MediaUtil;

import java.lang.ref.WeakReference;

public class PlayActivity extends BaseActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private TextView tv_song, tv_start, tv_end;
    private ImageView iv_album, iv_mode, iv_last, iv_play, iv_next, iv_favorite;
    private SeekBar sb_music;
    private final static int MSG_CHANGEUI = 0x1;
    private MyHandler myHandler;
    boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        //保存当前播放的一些状态
        myHandler = new MyHandler(this);
        isPause = getIntent().getBooleanExtra("isPause", false);
    }

    private void initView() {
        tv_song = (TextView) findViewById(R.id.tv_song);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_end = (TextView) findViewById(R.id.tv_end);
        iv_album = (ImageView) findViewById(R.id.iv_album);
        iv_mode = (ImageView) findViewById(R.id.iv_mode);
        iv_last = (ImageView) findViewById(R.id.iv_last);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_favorite = (ImageView) findViewById(R.id.iv_favorite);
        iv_album.setOnClickListener(this);
        iv_mode.setOnClickListener(this);
        iv_last.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_favorite.setOnClickListener(this);
        sb_music = (SeekBar) findViewById(R.id.sb_music);
        sb_music.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_last:
                this.playService.prev(this.playService.getCurrentPosition());
                break;
            case R.id.iv_play:
                if (this.playService.isPlaying()) {
                    iv_play.setImageResource(R.mipmap.play);
                    this.playService.pause();
                    isPause = true;
                } else {
                    if (isPause) {
                        iv_play.setImageResource(R.mipmap.pause);
                        this.playService.start();
                    } else {
                        this.playService.play(playService.getCurrentPosition());
                        iv_play.setImageResource(R.mipmap.pause);
                    }
                }
                break;
            case R.id.iv_next:
                this.playService.next();
                break;
            case R.id.iv_song:
                startActivity(new Intent(this, PlayActivity.class));
                break;
            case R.id.iv_mode:
                int mode = (int) iv_mode.getTag();
                switch (mode) {
                    case PlayService.ORDER_PLAY:
                        iv_mode.setImageResource(R.mipmap.random);
                        iv_mode.setTag(PlayService.RANDOM_PLAY);
                        playService.setPlayMode(PlayService.RANDOM_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.random_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.RANDOM_PLAY:
                        iv_mode.setImageResource(R.mipmap.single);
                        iv_mode.setTag(PlayService.SINGLE_PLAY);
                        playService.setPlayMode(PlayService.SINGLE_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.single_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.SINGLE_PLAY:
                        iv_mode.setImageResource(R.mipmap.order);
                        iv_mode.setTag(PlayService.ORDER_PLAY);
                        playService.setPlayMode(PlayService.ORDER_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.order_play), Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case R.id.iv_favorite:
                try {
                    Mp3Info mp3Info = playService.getMp3Infos().get(playService.getCurrentPosition());
                    Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class)
                            .where("mp3InfoId", "=", mp3Info.getId()));
                    if (likeMp3Info == null) {
                        mp3Info.setMp3InfoId(mp3Info.getId());
                        mp3Info.setIsLike(true);
                        app.dbUtils.save(mp3Info);
                        iv_favorite.setImageResource(R.mipmap.fav_t);
                        Toast.makeText(this, getString(R.string.fav_success), Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isLike = likeMp3Info.isLike();
                        if (isLike){
                            likeMp3Info.setIsLike(false);
                            iv_favorite.setImageResource(R.mipmap.fav_f);
                            Toast.makeText(this, getString(R.string.fav_cancle), Toast.LENGTH_SHORT).show();
                        }else{
                            likeMp3Info.setIsLike(true);
                            iv_favorite.setImageResource(R.mipmap.fav_t);
                            Toast.makeText(this, getString(R.string.fav_success), Toast.LENGTH_SHORT).show();
                        }
                        app.dbUtils.update(likeMp3Info, "isLike");
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void publish(int progress) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG_CHANGEUI, progress));
    }

    @Override
    public void change(int position) {
//        if (playService.isPlaying()) {}
        Mp3Info mp3Info = playService.getMp3Infos().get(position);
        tv_song.setText(mp3Info.getTitle());
        Bitmap bitmap = MediaUtil.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
        iv_album.setImageBitmap(bitmap);
        tv_end.setText(MediaUtil.formatTime(mp3Info.getDuration()));
        sb_music.setProgress(0);
        sb_music.setMax((int) mp3Info.getDuration());
        if (playService.isPlaying()) {
            iv_play.setImageResource(R.mipmap.pause);
        } else {
            iv_play.setImageResource(R.mipmap.play);
        }
        switch (playService.getPlayMode()) {
            case PlayService.ORDER_PLAY:
                iv_mode.setImageResource(R.mipmap.order);
                iv_mode.setTag(PlayService.ORDER_PLAY);
                break;
            case PlayService.RANDOM_PLAY:
                iv_mode.setImageResource(R.mipmap.random);
                iv_mode.setTag(PlayService.RANDOM_PLAY);
                break;
            case PlayService.SINGLE_PLAY:
                iv_mode.setImageResource(R.mipmap.single);
                iv_mode.setTag(PlayService.SINGLE_PLAY);
                break;
        }

        try {
            Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class)
                    .where("mp3InfoId", "=", mp3Info.getId()));
            if (likeMp3Info == null) {
                iv_favorite.setImageResource(R.mipmap.fav_f);
            } else {
                iv_favorite.setImageResource(R.mipmap.fav_t);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            playService.pause();
            playService.seekTo(progress);
            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    class MyHandler extends Handler {

        private WeakReference<Activity> reference;

        public MyHandler(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference.get() != null) {
                switch (msg.what) {
                    case MSG_CHANGEUI:
                        int progress = (int) msg.obj;
                        tv_start.setText(MediaUtil.formatTime(progress));
                        sb_music.setProgress(progress);
                        break;
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }
}
