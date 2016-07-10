package com.wonder.musicapp.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.wonder.musicapp.Activitys.BaseActivity;
import com.wonder.musicapp.Entitys.SearchResult;
import com.wonder.musicapp.R;
import com.wonder.musicapp.Utils.DownloadUtils;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadDialogFragment extends DialogFragment {

    private SearchResult searchResult;
    private BaseActivity activity;
    private String[] items;

    public static DownloadDialogFragment newInstance(SearchResult searchResult) {
        DownloadDialogFragment downloadDialogFragment = new DownloadDialogFragment();
        downloadDialogFragment.searchResult = searchResult;
        return downloadDialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (BaseActivity) activity;
        items = new String[]{getString(R.string.play),getString(R.string.download), getString(R.string.cancel)};
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //执行下载
                        playMusic();
                        break;
                    case 1:
                        //执行下载
                        downloadMusic();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        return builder.show();
    }

    private void playMusic() {

    }

    private void downloadMusic() {
        Toast.makeText(activity, getString(R.string.downloading) + ":" + searchResult.getMusicName(), Toast.LENGTH_SHORT).show();
        DownloadUtils.getInstance().setListener(new DownloadUtils.onDownloadListener() {
            @Override
            public void onDownload(String mp3Url) {//下载成功
                Toast.makeText(activity, getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                //扫描新下载的歌曲
                Uri contentUri = Uri.fromFile(new File(mp3Url));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
                activity.sendBroadcast(mediaScanIntent);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
            }
        }).download(searchResult);
    }
}
