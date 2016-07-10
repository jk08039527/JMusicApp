package com.wonder.musicapp.Activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.wonder.musicapp.Adapters.MyMusicAdapter;
import com.wonder.musicapp.Constant;
import com.wonder.musicapp.Entitys.Mp3Info;
import com.wonder.musicapp.R;

import java.util.ArrayList;
import java.util.List;

public class RecentMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView lv_recent;
    private ArrayList<Mp3Info> recentMp3Infos;
    private MyMusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_music);
        initView();
    }

    private void initView() {
        lv_recent = (ListView) findViewById(R.id.lv_recent);
        initData();
        lv_recent.setOnItemClickListener(this);
    }

    private void initData() {
        try {
            List<Mp3Info> list = app.dbUtils.findAll(Selector.from(Mp3Info.class)
                    .where("playTime", "!=", 0).orderBy("playTime", true).limit(Constant.PLAY_RECORD_NUM));
            recentMp3Infos = (ArrayList<Mp3Info>) list;
            if (recentMp3Infos != null && recentMp3Infos.size() != 0) {
                adapter = new MyMusicAdapter(this, recentMp3Infos);
                lv_recent.setAdapter(adapter);
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
        if (playService.getChangePlayList() != playService.RECENT_MUSIC_LIST) {
            playService.setMp3Infos(recentMp3Infos);
            playService.setChangePlayList(playService.RECENT_MUSIC_LIST);
        }
        playService.play(position);
    }
}
