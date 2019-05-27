package com.wolcano.musicplayer.music.content.slidingpanel;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.content.Binder;
import com.wolcano.musicplayer.music.widgets.ScaledImageView;
import com.wolcano.musicplayer.music.mvp.listener.OnServiceListener;
import com.wolcano.musicplayer.music.mvp.listener.OnSwipeTouchListener;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.widgets.SongCover;
import com.wolcano.musicplayer.music.mvp.listener.Bind;
import com.wolcano.musicplayer.music.ui.activity.MainActivity;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class SlidingPanel implements OnServiceListener,View.OnClickListener {


    private Disposable disposable;
    private Drawable placeholder;
    private Activity activity;


    @Bind(R.id.progressTop1)
    private ProgressBar progressBar;
    @Bind(R.id.line1)
    private TextView line1;
    @Bind(R.id.play)
    private ImageView play;
    @Bind(R.id.line2)
    private TextView line2;
    @Bind(R.id.panel_top1bg)
    private ImageView panelTop1;
    @Bind(R.id.model_imageview)
    private ImageView modelImage;


    public SlidingPanel(View view, Activity activity) {
        Binder.bindIt(this, view);
        this.activity = activity;
        setViews();
    }


    public void setViews(){
        // Marquee TextView
        line1.setHorizontallyScrolling(true);
        line1.setSelected(true);

        play.setColorFilter(Utils.getAccentColor(activity));


        if(RemotePlay.get().getPlayMusic(activity.getApplicationContext())!=null){
            String contentURI = "content://media/external/audio/media/" + RemotePlay.get().getPlayMusic(activity.getApplicationContext()).getSongId() + "/albumart";
            Picasso.get()
                    .load(contentURI)
                    .into(modelImage);
            loadBitmap(RemotePlay.get().getPlayMusic(activity.getApplicationContext()),panelTop1);

        }
        ScaledImageView scaledImageView = new ScaledImageView(activity);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        scaledImageView.imageBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(activity.getResources(),R.drawable.album_default),width,height,true);
        placeholder = activity.getResources().getDrawable(R.drawable.album_default);
        placeholder.setColorFilter(Utils.getPrimaryColor(activity), PorterDuff.Mode.MULTIPLY);

        progressBar.getProgressDrawable().setColorFilter(Utils.getAccentColor(activity), PorterDuff.Mode.SRC_IN);
        play.setOnClickListener(this);
        panelTop1.setOnTouchListener(new OnSwipeTouchListener(activity) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
            }

            @Override
            public void onClick() {
                ((MainActivity) activity).expandPanel();
            }
        });
        onChangeSong(RemotePlay.get().getPlayMusic(activity.getApplicationContext()));
    }
    @Override
    public void onPlayStart() {
        play.setSelected(true);

    }

    @Override
    public void onPlayPause() {
        play.setSelected(false);

    }


    public Disposable getDisposable() {
        return disposable;
    }


    @Override
    public void onProgressChange(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                RemotePlay.get().buttonClick(activity);
                break;
        }
    }
    private void setBitmap(Bitmap bitmap,ImageView imageView){
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
    }


    @Override
    public void onChangeSong(Song song) {
        if (song == null) {
            line1.setText("");
            line2.setText("");
            progressBar.setProgress(0);
            progressBar.setMax(0);
            Picasso.get()
                    .load(R.drawable.album_art)
                    .into(modelImage);

            loadBitmap(song,panelTop1);
            return;
        }
        String contentURI = "content://media/external/audio/media/" + song.getSongId() + "/albumart";
        Picasso.get()
                .load(contentURI)
                .placeholder(R.drawable.album_art)
                .into(modelImage);

        loadBitmap(song,panelTop1);

        line1.setText(song.getTitle());
        line2.setText(song.getArtist());
        play.setSelected(RemotePlay.get().isPlaying() || RemotePlay.get().isPreparing());
        progressBar.setMax((int) song.getDura());
        progressBar.setProgress((int) RemotePlay.get().getPlayerCurrentPosition());

    }

    private void loadBitmap(Song song, ImageView imageView) {

        Observable<Bitmap> bitmapObservable =
                Observable.fromCallable(() -> SongCover.get().loadBlurred(activity.getApplicationContext(),song)).throttleFirst(500, TimeUnit.MILLISECONDS);
        disposable = bitmapObservable.
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).

        subscribeWith(new DisposableObserver<Bitmap>(){


            @Override
            public void onNext(Bitmap bitmap) {
                setBitmap(bitmap,imageView);
            }

            @Override
            public void onError(Throwable e) {
                imageView.setImageDrawable(placeholder);
            }

            @Override
            public void onComplete() {
            }
        });
    }


}
