package com.wolcano.musicplayer.music.content

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.listener.Bind
import com.wolcano.musicplayer.music.listener.OnServiceListener
import com.wolcano.musicplayer.music.listener.OnSwipeTouchListener
import com.wolcano.musicplayer.music.provider.RemotePlay.buttonClick
import com.wolcano.musicplayer.music.provider.RemotePlay.getPlayMusic
import com.wolcano.musicplayer.music.provider.RemotePlay.getPlayerCurrentPosition
import com.wolcano.musicplayer.music.provider.RemotePlay.isPlaying
import com.wolcano.musicplayer.music.provider.RemotePlay.isPreparing
import com.wolcano.musicplayer.music.ui.activity.main.MainActivity
import com.wolcano.musicplayer.music.utils.Utils
import com.wolcano.musicplayer.music.widgets.ScaledImageView
import com.wolcano.musicplayer.music.widgets.SongCover
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SlidingPanel(view: View, private val activity: Activity) : OnServiceListener,
    View.OnClickListener {

    var disposable: Disposable? = null
    private var placeholderDrawable: Drawable? = null

    @Bind(R.id.progressTop1)
    private lateinit var progressBar: ProgressBar

    @Bind(R.id.txt_line_1)
    private lateinit var line1: TextView

    @Bind(R.id.img_play)
    private lateinit var play: ImageView

    @Bind(R.id.txt_line_2)
    private lateinit var line2: TextView

    @Bind(R.id.panel_top1bg)
    private lateinit var slidingUpPanelTop1: ImageView

    @Bind(R.id.model_imageview)
    private lateinit var modelImage: ImageView

    init {
        Binder.bindIt(this, view)
        setViews()
    }

    fun setViews() {
        // Marquee TextView
        line1.setHorizontallyScrolling(true)
        line1.isSelected = true
        play.setColorFilter(Utils.getAccentColor(activity))
        if (getPlayMusic(activity.applicationContext) != null) {
            val contentURI = "content://media/external/audio/media/" + getPlayMusic(
                activity.applicationContext
            )?.songId + "/albumart"
            Picasso.get()
                .load(contentURI)
                .into(modelImage)
            loadBitmap(getPlayMusic(activity.applicationContext), slidingUpPanelTop1)
        }
        val scaledImageView = ScaledImageView(activity)
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        scaledImageView.imageBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                activity.resources, R.drawable.album_default
            ), width, height, true
        )

        placeholderDrawable = ContextCompat.getDrawable(activity, R.drawable.album_default)
        placeholderDrawable?.setColorFilter(
            Utils.getPrimaryColor(activity),
            PorterDuff.Mode.MULTIPLY
        )
        progressBar.progressDrawable.setColorFilter(
            Utils.getAccentColor(activity),
            PorterDuff.Mode.SRC_IN
        )
        play.setOnClickListener(this)
        slidingUpPanelTop1.setOnTouchListener(object : OnSwipeTouchListener(activity) {
            override fun onClick() {
                super.onClick()
                (activity as MainActivity).expandPanel()
            }
        })

        onChangeSong(getPlayMusic(activity.applicationContext))
    }

    override fun onPlayStart() {
        play.isSelected = true
    }

    override fun onPlayPause() {
        play.isSelected = false
    }

    override fun onProgressChange(progress: Int) {
        progressBar.progress = progress
    }

    override fun onBufferingUpdate(percent: Int) {}

    override fun onClick(v: View) {
        when (v.id) {
            R.id.play -> buttonClick(activity)
        }
    }

    private fun setBitmap(bitmap: Bitmap?, imageView: ImageView) {
        if (bitmap != null) imageView.setImageBitmap(bitmap)
    }

    override fun onChangeSong(song: Song?) {
        if (song == null) {
            line1.text = ""
            line2.text = ""
            progressBar.progress = 0
            progressBar.max = 0
            Picasso.get()
                .load(R.drawable.album_art)
                .into(modelImage)
            loadBitmap(song, slidingUpPanelTop1)
            return
        }
        val contentURI = "content://media/external/audio/media/" + song.songId + "/albumart"
        Picasso.get()
            .load(contentURI)
            .placeholder(R.drawable.album_art)
            .into(modelImage)
        loadBitmap(song, slidingUpPanelTop1)
        line1.text = song.title
        line2.text = song.artist
        play.isSelected = isPlaying() || isPreparing()
        progressBar.max = song.duration.toInt()
        progressBar.progress = getPlayerCurrentPosition().toInt()
    }

    private fun loadBitmap(song: Song?, imageView: ImageView) {
        val bitmapObservable = Observable.fromCallable {
            SongCover.loadBlurred(
                activity.applicationContext, song
            )
        }.throttleFirst(500, TimeUnit.MILLISECONDS)
        disposable =
            bitmapObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Bitmap?>() {
                    override fun onNext(bitmap: Bitmap) {
                        setBitmap(bitmap, imageView)
                    }

                    override fun onError(e: Throwable) {
                        imageView.setImageDrawable(placeholderDrawable)
                    }

                    override fun onComplete() {}
                })
    }

}