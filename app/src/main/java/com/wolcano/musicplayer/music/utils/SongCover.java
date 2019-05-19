package com.wolcano.musicplayer.music.utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import androidx.collection.LruCache;
import android.text.TextUtils;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.ModelBitmap;
import com.wolcano.musicplayer.music.mvp.models.Song;

import java.io.FileDescriptor;
import java.util.HashMap;
import java.util.Map;


public class SongCover {

    public enum Tip {
        BLURRED
        ,OVAL
    }

    private static final String NULL_VAL = "null";
    private Map<Tip, LruCache<String, Bitmap>> bitmapStore;
    private int ovalSize;

    public static SongCover get() {
        return SingletonHolder.singletonInstance;
    }

    private static class SingletonHolder {
        private static SongCover singletonInstance = new SongCover();
    }

    public void init(Context context) {
        ovalSize = Utils.getDeviceScrWidth(context) / 2;
        LruCache<String, Bitmap> ovalStore = new LruCache<>(10);
        LruCache<String, Bitmap> blurredStore = new LruCache<>(10);
        bitmapStore = new HashMap<>(3);
        bitmapStore.put(Tip.OVAL, ovalStore);
        bitmapStore.put(Tip.BLURRED, blurredStore);
    }


    public void setOvalLength(int roundLength) {
        if (this.ovalSize != roundLength) {
            this.ovalSize = roundLength;
            bitmapStore.get(Tip.OVAL).evictAll();
        }
    }
    public Bitmap loadOval(Context context,Song song) {
        return loadCoverBitmap(context,song, Tip.OVAL);
    }
    public ModelBitmap loadOvalModel(Context context,Song song) {
        return loadCoverModelBitmap(context,song, Tip.OVAL);
    }

    public Bitmap loadBlurred(Context context,Song song) {
        return loadCoverBitmap(context,song, Tip.BLURRED);
    }
    private Bitmap loadModelFile(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, bmOptions);
    }
    public ModelBitmap loadBlurredModel(Context context,Song song) {
        return loadCoverModelBitmap(context,song, Tip.BLURRED);
    }

    public void setCacheDefault(Context context) {
        Bitmap bm;
        LruCache<String, Bitmap> bmStore = bitmapStore.get(Tip.BLURRED);
        bm = getMainModel(context,Tip.BLURRED);
        bmStore.put(NULL_VAL, bm);
    }

    private ModelBitmap loadCoverModelBitmap(Context context, Song song, Tip type) {
        ModelBitmap modelBitmap = new ModelBitmap();
        Bitmap bm;
        String strKey = getStrKey(song);
        LruCache<String, Bitmap> bmStore = bitmapStore.get(type);
        if (TextUtils.isEmpty(strKey)) {
            bm = bmStore.get(NULL_VAL);

            if (bm != null) {
                modelBitmap.setId(1);
                modelBitmap.setBitmap(bm);
                return modelBitmap;
            }
            bm = getMainModel(context,type);
            bmStore.put(NULL_VAL, bm);
            modelBitmap.setId(1);
            modelBitmap.setBitmap(bm);
            return modelBitmap;
        }
        bm = bmStore.get(strKey);
        if (bm != null) {
            modelBitmap.setId(0);
            modelBitmap.setBitmap(bm);
            return modelBitmap;
        }
        bm = loadModelwT(context,song, type);
        if (bm != null) {
            modelBitmap.setId(0);
            modelBitmap.setBitmap(bm);
            bmStore.put(strKey, bm);
            return modelBitmap;
        }
        return null;
    }
    private Bitmap getAlbumart(Context context,Long album_id) {
        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }
    private Bitmap loadCoverBitmap(Context context, Song song, Tip type) {
        Bitmap bm;
        String strKey = getStrKey(song);
        LruCache<String, Bitmap> bmStore = bitmapStore.get(type);

        if (TextUtils.isEmpty(strKey)) {
            bm = bmStore.get(NULL_VAL);

            if (bm != null) {
                return bm;
            }
            bm = getMainModel(context,type);
            bmStore.put(NULL_VAL, bm);
            return bm;
        }

        bm = bmStore.get(strKey);
        if (bm != null) {
            return bm;
        }

        bm = loadModelwT(context,song, type);
        if (bm != null) {
            bmStore.put(strKey, bm);
            return bm;
        }

        return null;
    }
    private String getStrKey(Song song) {
        if (song == null) {
            return null;
        }
        if (song.getTip() == Song.Tip.MODEL0 && song.getAlbumId() > 0) {
            return String.valueOf(song.getAlbumId());
        } else if (song.getTip() == Song.Tip.MODEL1 && !TextUtils.isEmpty(song.getCovPath())) {
            return song.getCovPath();
        } else {
            return null;
        }
    }

    public Bitmap getMainModel(Context context,Tip tip) {
        switch (tip) {
            case BLURRED:
                Bitmap bitmap1 = ImageUtils.tintBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.album_default), Utils.getPrimaryColor(context));
                return bitmap1;
            case OVAL:
                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.album_art_round);
                bm = ImageUtils.chgImage(bm, ovalSize, ovalSize);
                return bm;
            default:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.album_art);
        }
    }


    private Bitmap loadModelwT(Context context,Song song, Tip tip) {
        Bitmap bm;
        if (song.getTip() == Song.Tip.MODEL0) {
            bm = getAlbumart(context,song.getAlbumId());
        } else {
            bm = loadModelFile(song.getCovPath());
        }
        switch (tip) {
            case BLURRED:
                return ImageUtils.blur(bm);
            case OVAL:
                bm = ImageUtils.chgImage(bm, ovalSize, ovalSize);
                return ImageUtils.appendOvalImg(bm);
            default:
                return bm;
        }
    }




}
