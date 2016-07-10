package com.wonder.musicapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.wonder.musicapp.Activitys.BaseActivity;
import com.wonder.musicapp.Activitys.PlayActivity;
import com.wonder.musicapp.Adapters.MyMusicAdapter;
import com.wonder.musicapp.Entitys.Mp3Info;
import com.wonder.musicapp.R;
import com.wonder.musicapp.Utils.MediaUtil;

import java.util.ArrayList;

public class MusicListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView lv_music;
    private ArrayList<Mp3Info> mp3Infos;
    private BaseActivity activity;
    private boolean isPause = false;

    //UI界面
    private TextView tv_singer, tv_sing_name;
    private ImageView iv_song;
    private ImageView iv_last, iv_play, iv_next;

    public MusicListFragment() {
    }

    public static MusicListFragment newInstance() {
        MusicListFragment fragment = new MusicListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, null);
        initView(view);
//        loadData();
        return view;
    }

    private void initView(View view) {
        lv_music = (ListView) view.findViewById(R.id.lv_music);
        lv_music.setOnItemClickListener(this);
        tv_singer = (TextView) view.findViewById(R.id.tv_singer);
        tv_sing_name = (TextView) view.findViewById(R.id.tv_sing_name);
        iv_song = (ImageView) view.findViewById(R.id.iv_song);
        iv_last = (ImageView) view.findViewById(R.id.iv_last);
        iv_play = (ImageView) view.findViewById(R.id.iv_play);
        iv_next = (ImageView) view.findViewById(R.id.iv_next);
        iv_last.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_song.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_last:
                activity.playService.prev(activity.playService.getCurrentPosition());
                break;
            case R.id.iv_play:
                if (activity.playService.isPlaying()) {
                    iv_play.setImageResource(R.mipmap.play);
                    activity.playService.pause();
                    isPause = true;
                } else {
                    if (isPause) {
                        iv_play.setImageResource(R.mipmap.pause);
                        activity.playService.start();
                    } else {
                        activity.playService.play(activity.playService.getCurrentPosition());
                        iv_play.setImageResource(R.mipmap.pause);
                    }
                }
                break;
            case R.id.iv_next:
                activity.playService.next();
                break;
            case R.id.iv_song:
                Intent intent = new Intent(activity, PlayActivity.class);
                intent.putExtra("isPause", isPause);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (BaseActivity) activity;
        System.out.println("MusicListFragment/onAttach");
    }

    /**
     * 加载本地音乐列表
     */
    public void loadData() {
        mp3Infos = MediaUtil.getMp3Infos(activity);
        if (mp3Infos == null || mp3Infos.size() == 0) {
            Toast.makeText(activity, "歌曲列表为空！", Toast.LENGTH_SHORT).show();
        } else {
            MyMusicAdapter musicAdapter = new MyMusicAdapter(activity, mp3Infos);
            lv_music.setAdapter(musicAdapter);
        }
    }

    public void changeUIStatus(int position) {
        mp3Infos = activity.playService.getMp3Infos();
        if (mp3Infos==null){
            return;
        }
        if (position >= 0 && position < mp3Infos.size()) {
            Mp3Info mp3Info = activity.playService.getMp3Infos().get(position);
            tv_sing_name.setText(mp3Info.getTitle());
            tv_singer.setText(mp3Info.getArtist());
            Bitmap albumBitmap = MediaUtil.getArtwork(activity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
            iv_song.setImageBitmap(albumBitmap);
            if (activity.playService.isPlaying()) {
                iv_play.setImageResource(R.mipmap.pause);
            } else {
                iv_play.setImageResource(R.mipmap.play);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (activity.playService.getChangePlayList() != activity.playService.MY_MUSIC_LIST) {
            activity.playService.setMp3Infos(mp3Infos);
            activity.playService.setChangePlayList(activity.playService.MY_MUSIC_LIST);
        }
        activity.playService.play(position);

        //保存播放时间
        savePlayRecord();
    }

    private void savePlayRecord() {
        Mp3Info mp3Info = activity.playService.getMp3Infos().get(activity.playService.getCurrentPosition());
        try {
            Mp3Info playRecordMp3Info = BaseActivity.app.dbUtils.
                    findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", mp3Info.getId()));
            if (playRecordMp3Info == null) {
                mp3Info.setPlayTime(System.currentTimeMillis());
                BaseActivity.app.dbUtils.save(mp3Info);
            } else {
                playRecordMp3Info.setPlayTime(System.currentTimeMillis());
                BaseActivity.app.dbUtils.update(playRecordMp3Info, "playTime");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unbindPlayService();
    }
}
