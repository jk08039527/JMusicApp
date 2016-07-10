package com.wonder.musicapp.Fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wonder.musicapp.Activitys.BaseActivity;
import com.wonder.musicapp.Adapters.NetMusicAdapter;
import com.wonder.musicapp.Constant;
import com.wonder.musicapp.Entitys.SearchResult;
import com.wonder.musicapp.R;
import com.wonder.musicapp.Utils.AppUtil;
import com.wonder.musicapp.Utils.SearchMusicUtils;
import com.wonder.musicapp.View.InputEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NetMusicFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private BaseActivity activity;
    private InputEditText et_search;
    private ListView lv_music;
    private ArrayList<SearchResult> searchResults;
    private ProgressBar pb_load;
    private NetMusicAdapter adapter;
    private int page = 1;//搜索页码

    public NetMusicFragment() {
    }

    public static NetMusicFragment newInstance() {
        NetMusicFragment fragment = new NetMusicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResults = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net_music, null);
        initView(view);
//        loadData();
        return view;
    }

    private void initView(View view) {
        lv_music = (ListView) view.findViewById(R.id.lv_music);
        lv_music.setOnItemClickListener(this);
        pb_load = (ProgressBar) view.findViewById(R.id.pb_load);
        et_search = (InputEditText) view.findViewById(R.id.et_search);
        et_search.setSearchClickListener(this);
        loadNetData();
    }

    private void loadNetData() {
        pb_load.setVisibility(View.VISIBLE);
        //执行网络异步任务
        new LoadNetDataTask().execute(Constant.BAIDU_URL + Constant.BAIDU_DAYHOT);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (BaseActivity) activity;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (position >= adapter.getSearchResults().size() || position < 0) return;
        showDownloadDialog(position);
    }

    private void showDownloadDialog(int position) {
        DownloadDialogFragment downloadDialogFragment = DownloadDialogFragment.newInstance(searchResults.get(position));
        downloadDialogFragment.show(getFragmentManager(), "download");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                searchMusic();
                break;

        }
    }

    //搜索音乐
    private void searchMusic() {
        //隐藏输入法
        AppUtil.hideInputMethod(et_search);
        String key = et_search.getText().toString();
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(activity, "请输入歌名", Toast.LENGTH_SHORT).show();
            return;
        }
        SearchMusicUtils.getInstance().setListener(new SearchMusicUtils.onSearchResultListener() {
            @Override
            public void onSearchResult(ArrayList<SearchResult> results) {
                ArrayList<SearchResult> sr = adapter.getSearchResults();
                sr.clear();
                sr.addAll(results);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSearchFailed(String results) {
                Toast.makeText(activity, results, Toast.LENGTH_SHORT).show();
            }
        }).search(key, page);


    }

    class LoadNetDataTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_load.setVisibility(View.VISIBLE);
            searchResults.clear();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            try {
                //使用Jsoup组件请求网络，并解析音乐数据
                Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(Constant.TIMEOUT).get();
                Elements songTitles = doc.select("span.song-title");
                Elements artists = doc.select("span.author_list");
                for (int i = 0; i < songTitles.size() - 1; i++) {
                    SearchResult searchResult = new SearchResult();
                    Elements urls = songTitles.get(i).getElementsByTag("a");
                    searchResult.setUrl(urls.get(0).attr("href"));
                    searchResult.setMusicName(urls.get(0).text());
                    Elements artistElements = artists.get(i).getElementsByTag("a");
                    searchResult.setArtist(artistElements.get(0).text());
                    searchResult.setAlbum("热歌榜");
                    searchResults.add(searchResult);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("网络连接有问题");
                return -1;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1 && searchResults.size() > 0) {
                adapter = new NetMusicAdapter(activity, searchResults);
                lv_music.setAdapter(adapter);
            } else {
                Toast.makeText(activity, "您的网络连接有问题", Toast.LENGTH_SHORT).show();
            }
            pb_load.setVisibility(View.GONE);
        }
    }
}