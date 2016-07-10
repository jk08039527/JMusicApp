package com.wonder.musicapp.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.wonder.musicapp.Entitys.Mp3Info;
import com.wonder.musicapp.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/25.
 */
public class MediaUtil {
    private static final String TAG = "MediaUtil——————";
    private static final Uri albumArtUri = Uri.parse
            ("content://media/external/audio/albumart");

    /**
     * 根据id从数据库中查询歌曲的信息，封装在mp3Info中返回
     *
     * @param context
     * @param _id
     * @return
     */
    public static Mp3Info getMp3Info(Context context, long _id) {
        Log.d(TAG, String.valueOf(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Audio.Media._ID + "=" + _id, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        Mp3Info mp3Info = null;
        if (cursor.moveToNext()) {
            mp3Info = new Mp3Info();
            long id = cursor.getLong(cursor.getColumnIndex
                    (MediaStore.Audio.Media._ID));//音乐id
            String title = cursor.getString(cursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE));//标题
            String artist = cursor.getString(cursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST));//艺术家
            String album = cursor.getString(cursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM));//专辑
            long album_id = cursor.getLong(cursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID));//专辑id
            long duration = cursor.getLong(cursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor.getColumnIndex
                    (MediaStore.Audio.Media.SIZE));//大小
            String url = cursor.getString(cursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA));//文件路径
            int isMusic = cursor.getInt(cursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_MUSIC));//文件路径
            if (isMusic != 0) {
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbum(album);
                mp3Info.setAlbumId(album_id);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
            }
        }
        cursor.close();
        return mp3Info;
    }

    /**
     * 从数据库中查询歌曲的信息，保存在List中返回
     *
     * @param context
     * @return
     */
    public static ArrayList<Mp3Info> getMp3Infos(Context context) {
        Log.d(TAG, String.valueOf(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Audio.Media.DURATION + ">=180000", null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        ArrayList<Mp3Info> mp3Infos = null;
        if (cursor != null) {
            int count = cursor.getCount();
            mp3Infos = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                cursor.moveToNext();
                Mp3Info mp3Info = new Mp3Info();
                long id = cursor.getLong(cursor.getColumnIndex
                        (MediaStore.Audio.Media._ID));//音乐id
                String title = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Audio.Media.TITLE));//标题
                String artist = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST));//艺术家
                String album = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM));//专辑
                long album_id = cursor.getLong(cursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM_ID));//专辑id
                long duration = cursor.getLong(cursor.getColumnIndex
                        (MediaStore.Audio.Media.DURATION));//时长
                long size = cursor.getLong(cursor.getColumnIndex
                        (MediaStore.Audio.Media.SIZE));//大小
                String url = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Audio.Media.DATA));//文件路径
                int isMusic = cursor.getInt(cursor.getColumnIndex
                        (MediaStore.Audio.Media.IS_MUSIC));//文件路径
                if (isMusic != 0) {
                    mp3Info.setId(id);
                    mp3Info.setTitle(title);
                    mp3Info.setArtist(artist);
                    mp3Info.setAlbum(album);
                    mp3Info.setAlbumId(album_id);
                    mp3Info.setDuration(duration);
                    mp3Info.setSize(size);
                    mp3Info.setUrl(url);
                }
                mp3Infos.add(mp3Info);
            }
            cursor.close();
        }
        return mp3Infos;
    }

    /**
     * 从数据库中查询歌曲的信息，保存在List中返回
     *
     * @param context
     * @return
     */
    public static long[] getMp3InfoIds(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID}, MediaStore.Audio.Media.DURATION + ">=10000",
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        long[] ids = null;
        if (cursor != null) {
            int all = cursor.getCount();
            ids = new long[all];
            for (int i = 0; i < all; i++) {
                cursor.moveToNext();
                ids[i] = cursor.getLong(0);
            }
        }
        cursor.close();
        return ids;
    }

    /**
     * 通过总秒数返回正常格式
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + time % (1000 * 60) + "";
        } else if (sec.length() == 3) {
            sec = "00" + time % (1000 * 60) + "";
        } else if (sec.length() == 2) {
            sec = "000" + time % (1000 * 60) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + time % (1000 * 60) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    /**
     * 获取默认图片
     *
     * @param context
     * @param small
     * @return
     */
    public static Bitmap getdefaultArtWork(Context context, boolean small) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        if (small) {//返回小图片
            return BitmapFactory.decodeStream(context.getResources()
                    .openRawResource(R.mipmap.music_album), null, options);
        }
        return BitmapFactory.decodeStream(context.getResources()
                .openRawResource(R.mipmap.music_album), null, options);
    }

    public static Bitmap getArtwork(Context context, long song_id,
                                    long album_id, boolean allowDefault, boolean small) {
        if (album_id < 0) {
            if (song_id < 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowDefault) {
                return getdefaultArtWork(context, small);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //先制定原始大小
                options.inSampleSize = 1;
                //只进行大小判断
                options.inJustDecodeBounds = true;
                //调用此方法得到option得到的图片大小
                BitmapFactory.decodeStream(in, null, options);
                //我们的目标是在画面上显示，所以要调用的computerSampleSize得到图片的缩放比例
                //这里的target为800是根据默认专辑图片大小决定的，800只是调试的数字但是试验后发现完美的结合
//                if(small){
//                    options.inSampleSize = compu
//                }
                //我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowDefault) {
                            return getdefaultArtWork(context, small);
                        }
                    }
                } else if (allowDefault) {
                    bm = getdefaultArtWork(context, small);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return getdefaultArtWork(context, small);
    }

    public static Bitmap getArtworkFromFile(Context context, long song_id, long album_id) {
        Bitmap bm = null;
        if (album_id < 0 && song_id < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        FileDescriptor fd = null;
        try {
            if (album_id < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media" + song_id + "albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
                //先制定原始大小
                options.inSampleSize = 1;
                //只进行大小判断
                options.inJustDecodeBounds = true;
                //调用此方法得到option得到的图片大小
                BitmapFactory.decodeFileDescriptor(fd, null, options);

                options.inSampleSize = 100;
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }
}
