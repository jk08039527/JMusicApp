package com.wonder.musicapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wonder.musicapp.Entitys.SearchResult;
import com.wonder.musicapp.R;
import com.wonder.musicapp.Utils.MediaUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/19.
 */
public class NetMusicAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SearchResult> searchResults;

    public NetMusicAdapter(Context context, ArrayList<SearchResult> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int i) {
        return searchResults.get(i);
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
            vh.tv_time.setVisibility(View.GONE);
            vh.iv_song = (ImageView) view.findViewById(R.id.iv_song);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        SearchResult searchResult = searchResults.get(i);
        vh.tv_sing_name.setText(searchResult.getMusicName());
        vh.tv_singer.setText(searchResult.getArtist());
//        vh.tv_time.setText(MediaUtil.formatTime(searchResult.getDuration()));
        vh.iv_song.setImageBitmap(MediaUtil.getdefaultArtWork(context, true));
        return view;
    }

    static class ViewHolder {
        TextView tv_sing_name;
        TextView tv_singer;
        TextView tv_time;
        ImageView iv_song;
    }
}
