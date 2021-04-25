package com.wolcano.musicplayer.music.widgets

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.collection.LruCache
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.ModelBitmap
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.utils.ImageUtils
import com.wolcano.musicplayer.music.utils.Utils.getDeviceScrWidth
import com.wolcano.musicplayer.music.utils.Utils.getPrimaryColor
import java.util.*

object SongCover {

    enum class Tip {
        BLURRED, OVAL
    }

    private val NULL_VAL = "null"
    private val bitmapStore = HashMap<Tip, LruCache<String, Bitmap>>(3)
    private var ovalSize = 0

    fun init(context: Context?) {
        ovalSize = getDeviceScrWidth(context!!) / 2
        bitmapStore[Tip.OVAL] = LruCache<String, Bitmap>(10)
        bitmapStore[Tip.BLURRED] = LruCache<String, Bitmap>(10)
    }

    fun setOvalLength(roundLength: Int) {
        if (ovalSize != roundLength) {
            ovalSize = roundLength
            bitmapStore[Tip.OVAL]?.evictAll()
        }
    }

    fun loadOval(context: Context, song: Song?): Bitmap? {
        return loadCoverBitmap(context, song, Tip.OVAL)
    }

    fun loadOvalModel(context: Context, song: Song?): ModelBitmap? {
        return loadCoverModelBitmap(context, song, Tip.OVAL)
    }

    fun loadBlurred(context: Context, song: Song?): Bitmap? {
        return loadCoverBitmap(context, song, Tip.BLURRED)
    }

    private fun loadModelFile(path: String): Bitmap? {
        val bmOptions = BitmapFactory.Options()
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeFile(path, bmOptions)
    }

    fun loadBlurredModel(context: Context, song: Song?): ModelBitmap? {
        return loadCoverModelBitmap(context, song, Tip.BLURRED)
    }

    fun setCacheDefault(context: Context) {
        val bmStore = bitmapStore[Tip.BLURRED]
        val bm: Bitmap = getMainModel(context, Tip.BLURRED)
        bmStore?.put(NULL_VAL, bm)
    }

    private fun loadCoverModelBitmap(context: Context, song: Song?, type: Tip): ModelBitmap? {
        var bm: Bitmap?
        val strKey: String? = getStrKey(song)
        val bmStore: LruCache<String, Bitmap>? =
            bitmapStore[type]
        if (TextUtils.isEmpty(strKey)) {
            bm = bmStore!![NULL_VAL]
            if (bm != null) {
                return ModelBitmap(1, bm)
            }
            bm = getMainModel(context, type)
            bmStore.put(NULL_VAL, bm)
            return ModelBitmap(1, bm)
        }
        bm = bmStore!![strKey!!]
        if (bm != null) {
            return ModelBitmap(0, bm)
        }
        bm = loadModelwT(context, song, type)
        if (bm != null) {
            bmStore.put(strKey, bm)
            return ModelBitmap(0, bm)
        }
        return null
    }

    private fun getAlbumart(context: Context, album_id: Long): Bitmap? {
        var bm: Bitmap? = null
        try {
            val sArtworkUri = Uri
                .parse("content://media/external/audio/albumart")
            val uri = ContentUris.withAppendedId(sArtworkUri, album_id)
            val pfd = context.contentResolver.openFileDescriptor(uri, "r")
            if (pfd != null) {
                val fd = pfd.fileDescriptor
                bm = BitmapFactory.decodeFileDescriptor(fd)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bm
    }

    private fun loadCoverBitmap(context: Context, song: Song?, type: Tip): Bitmap? {
        var bm: Bitmap?
        val strKey: String? = getStrKey(song)
        val bmStore: LruCache<String, Bitmap>? =
            bitmapStore[type]
        if (TextUtils.isEmpty(strKey)) {
            bm = bmStore!![NULL_VAL]
            if (bm != null) {
                return bm
            }
            bm = getMainModel(context, type)
            bmStore.put(NULL_VAL, bm)
            return bm
        }
        bm = bmStore!![strKey!!]
        if (bm != null) {
            return bm
        }
        bm = loadModelwT(context, song, type)
        if (bm != null) {
            bmStore.put(strKey, bm)
            return bm
        }
        return null
    }

    private fun getStrKey(song: Song?): String? {
        if (song == null) {
            return null
        }
        return if (song.type == Song.Tip.MODEL0 && song.albumId > 0) {
            song.albumId.toString()
        } else if (song.type == Song.Tip.MODEL1 && !TextUtils.isEmpty(song.covPath)) {
            song.covPath
        } else {
            null
        }
    }

    fun getMainModel(context: Context, tip: Tip?): Bitmap {
        return when (tip) {
            Tip.BLURRED -> {
                ImageUtils.tintBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.album_default),
                    getPrimaryColor(context)
                )
            }
            Tip.OVAL -> {
                var bm = BitmapFactory.decodeResource(context.resources, R.drawable.album_art_round)
                bm = ImageUtils.updateImage(bm, ovalSize, ovalSize)
                bm
            }
            else -> BitmapFactory.decodeResource(context.resources, R.drawable.album_art)
        }
    }

    private fun loadModelwT(context: Context, song: Song?, tip: Tip): Bitmap? {
        var bm: Bitmap?
        bm = if (song?.type == Song.Tip.MODEL0) {
            getAlbumart(context, song.albumId)
        } else {
            loadModelFile(song!!.covPath)
        }
        return when (tip) {
            Tip.BLURRED -> ImageUtils.blur(bm)
            Tip.OVAL -> {
                bm = ImageUtils.updateImage(bm, ovalSize, ovalSize)
                ImageUtils.appendOvalImg(bm)
            }
        }
    }

}