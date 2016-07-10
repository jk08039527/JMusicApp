package com.wonder.musicapp.Activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.wonder.musicapp.Adapters.MyMusicAdapter;
import com.wonder.musicapp.Entitys.Mp3Info;
import com.wonder.musicapp.R;

import java.util.ArrayList;
import java.util.List;

public class LikeMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView lv_mylike;
    private ArrayList<Mp3Info> likeMp3Infos;
    private MyMusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_music);
        initView();
    }

    private void initView() {
        lv_mylike = (ListView) findViewById(R.id.lv_mylike);
        initData();
        lv_mylike.setOnItemClickListener(this);
    }

    private void initData() {
        try {
            List<Mp3Info> list = app.dbUtils.findAll(Selector.from(Mp3Info
                    .class).where("isLike", "=", true));
            likeMp3Infos = (ArrayList<Mp3Info>) list;
            if (likeMp3Infos != null && likeMp3Infos.size() != 0) {
                adapter = new MyMusicAdapter(this, likeMp3Infos);
                lv_mylike.setAdapter(adapter);
            } else {
                Toast.makeText(this, getString(R.string.noMusic), Toast.LENGTH_SHORT).show();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (playService.getChangePlayList() != playService.LIKE_MUSIC_LIST) {
            playService.setMp3Infos(likeMp3Infos);
            playService.setChangePlayList(playService.LIKE_MUSIC_LIST);
        }
        playService.play(position);
        //保存播放时间
        savePlayRecord();
    }

    private void savePlayRecord() {
        Mp3Info mp3Info = playService.getMp3Infos().get(playService.getCurrentPosition());
        try {
            Mp3Info playRecordMp3Info = BaseActivity.app.dbUtils.
                    findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", mp3Info.getId()));
            if (playRecordMp3Info == null) {
                mp3Info.setMp3InfoId(mp3Info.getId());
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
}
