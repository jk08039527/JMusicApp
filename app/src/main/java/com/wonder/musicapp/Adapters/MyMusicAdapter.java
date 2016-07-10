package com.wonder.musicapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wonder.musicapp.Entitys.Mp3Info;
import com.wonder.musicapp.R;
import com.wonder.musicapp.Utils.MediaUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/25.
 */
public class MyMusicAdapter extends BaseAdapter {
    private Context context;

    public ArrayList<Mp3Info> getMp3Infos() {
        return mp3Infos;
    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    private ArrayList<Mp3Info> mp3Infos;

    public MyMusicAdapter(Context context, ArrayList<Mp3Info> mp3Infos) {
        this.context = context;
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int i) {
        return mp3Infos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_music, null);
            vh = new ViewHolder();
            vh.tv_sing_name = (TextView) view.findViewById(R.id.tv_sing_name);
            vh.tv_singer = (TextView) view.findViewById(R.id.tv_singer);
            vh.tv_time = (TextView) view.findViewById(R.id.tv_time);
            vh.iv_song = (ImageView) view.findViewById(R.id.iv_song);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        Mp3Info mp3Info = mp3Infos.get(i);
        vh.tv_sing_name.setText(mp3Info.getTitle());
        vh.tv_singer.setText(mp3Info.getArtist());
        vh.tv_time.setText(MediaUtil.formatTime(mp3Info.getDuration()));
        vh.iv_song.setImageBitmap(MediaUtil.getArtwork(context, mp3Info.getId(), mp3Info.getAlbumId(), true, true));
        return view;
    }

    static class ViewHolder {
        TextView tv_sing_name;
        TextView tv_singer;
        TextView tv_time;
        ImageView iv_song;
    }
}
