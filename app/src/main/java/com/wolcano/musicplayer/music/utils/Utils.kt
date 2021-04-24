package com.wolcano.musicplayer.music.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.Settings
import android.text.Html
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import com.afollestad.materialdialogs.MaterialDialog
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.ui.fragment.library.album.AlbumDetailFragment
import com.wolcano.musicplayer.music.ui.fragment.library.artist.ArtistDetailFragment
import com.wolcano.musicplayer.music.ui.fragment.library.genre.GenreDetailFragment
import com.wolcano.musicplayer.music.ui.fragment.playlist.PlaylistDetailFragment
import java.io.File
import java.util.*

object Utils {

    private lateinit var settings: SharedPreferences
    private var sLastConfig: Configuration? = null
    private var sElapsedFormatMMSS: String? = null
    private var sElapsedFormatHMMSS: String? = null
    private val sLock = Any()

    fun getPrimaryColor(context: Context): Int {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        var color = settings.getInt("color_theme", 0)
        if (color == 0) {
            color = ContextCompat.getColor(context, R.color.colorPrimary)
        }
        return color
    }

    fun setPrimaryColor(context: Context, color: Int) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putInt("color_theme", color).apply()
    }

    fun setAccentColor(context: Context, color: Int) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putInt("color_accent", color).apply()
    }

    fun getAccentColor(context: Context): Int {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        var color = settings.getInt("color_accent", 0)
        if (color == 0) {
            color = ContextCompat.getColor(context, R.color.colorAccent)
        }
        return color
    }

    fun getOpeningVal(context: Context): Int {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getInt("app_opening", 0)
    }

    fun setOpeningVal(context: Context, pos: Int) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putInt("app_opening", pos).apply()
    }

    fun getMobileInternet(context: Context): Boolean {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getBoolean("app_mobile_interet", false)
    }

    fun getPlaylistPos(context: Context): Int {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getInt("app_playlist_pos", 0)
    }

    fun savePlayPosition(context: Context, pos: Int) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putInt("app_playlist_pos", pos).apply()
    }

    fun getOpeningValSleep(context: Context): Int {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getInt("app_opening_sleep", 0)
    }

    fun setOpeningValSleep(context: Context, pos: Int) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putInt("app_opening_sleep", pos).apply()
    }

    fun setFirst(context: Context, `val`: Boolean) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putBoolean("app_howtouse", `val`).apply()
    }

    fun getFirst(context: Context): Boolean {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getBoolean("app_howtouse", true)
    }

    fun getAutoSearch(context: Context): Boolean {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getBoolean("app_auto_search", false)
    }

    fun setAutoSearch(context: Context, pos: Boolean) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putBoolean("app_auto_search", pos).apply()
    }

    fun getColorSelection(context: Context): Boolean {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        val bol = false
        return settings.getBoolean("app_color_selection", bol)
    }

    fun setColorSelection(context: Context, `val`: Boolean) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putBoolean("app_color_selection", `val`).apply()
    }

    fun getPlaylistId(context: Context): Int {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getInt("app_playlist_id", 0)
    }

    fun setPlaylistId(context: Context, `val`: Int) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putInt("app_playlist_id", `val`).apply()
    }

    fun getRecreated(context: Context): Boolean {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getBoolean("recreated_val", false)
    }

    fun setRecreated(context: Context, `val`: Boolean) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putBoolean("recreated_val", `val`).apply()
    }

    fun getCountDownload(context: Context): Int {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getInt("count_download", 0)
    }

    fun setCountDownload(context: Context, pos: Int) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putInt("count_download", pos).apply()
    }

    fun getLastSleepTimerValue(context: Context): Int {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getInt("last_sleep_timer_value", 30)
    }

    fun setLastSleepTimerValue(context: Context, value: Int) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putInt("last_sleep_timer_value", value).apply()
    }

    fun getNextSleepTimerElapsedRealTime(context: Context): Long {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getLong("next_sleep_timer_elapsed_real_time", -1)
    }

    fun setNextSleepTimerElapsedRealtime(context: Context, value: Long) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putLong("next_sleep_timer_elapsed_real_time", value).apply()
    }

    fun getStatHeight(context: Context): Int {
        var height = 0
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            height = context.resources.getDimensionPixelSize(resId)
        }
        return height
    }

    fun getDeviceScrWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.width
    }

    fun isColorLight(@ColorInt color: Int): Boolean {
        val darkness = 1.0 - (0.299 * Color.red(color).toDouble() + 0.587 * Color.green(color)
            .toDouble() + 0.114 * Color.blue(color).toDouble()) / 255.0
        return darkness < 0.4
    }

    private fun setLightStatusbar2(activity: Activity, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= 23) {
            val decorView = activity.window.decorView
            val systemUiVisibility = decorView.systemUiVisibility
            if (enabled) {
                decorView.systemUiVisibility = systemUiVisibility or 8192
            } else {
                decorView.systemUiVisibility = systemUiVisibility and -8193
            }
        }
    }

    private fun setLightStatusbarAuto(activity: Activity, bgColor: Int) {
        setLightStatusbar(activity, isColorLight(bgColor))
    }


    private fun setLightStatusbar(activity: Activity, enabled: Boolean) {
        setLightStatusbar2(activity, enabled)
    }

    fun rateWolcano(context: Context) {
        val url = Uri.parse("market://details?id=" + context.packageName)
        val intent = Intent(Intent.ACTION_VIEW, url)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        )
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    fun shareWolcano(context: Context) {
        val appId = context.applicationInfo.labelRes
        val pckg = context.packageName
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(appId))
        val appName = context.resources.getString(R.string.appname)
        val store = "https://play.google.com/store/apps/details?id=$pckg"
        intent.putExtra(Intent.EXTRA_TEXT, "$appName $store")
        val str = context.resources.getString(R.string.sharewolcano)
        context.startActivity(Intent.createChooser(intent, str))
    }

    fun setAllowDrawUnderStatusBar(window: Window) {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun setUpFastScrollRecyclerViewColor(recyclerView: FastScrollRecyclerView, accentColor: Int) {
        recyclerView.setPopupBgColor(accentColor)
        recyclerView.setThumbColor(accentColor)
        recyclerView.setTrackColor(Color.TRANSPARENT)
        if (ColorUtils.isColorLight(accentColor)) {
            recyclerView.setPopupTextColor(Color.BLACK)
        } else {
            recyclerView.setPopupTextColor(Color.WHITE)
        }
    }

    fun setRingtone(context: Context, id: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val content: CharSequence
                content = Html.fromHtml(
                    context.getString(
                        R.string.set_ringtone_message_first,
                        context.getString(R.string.appname)
                    )
                )
                MaterialDialog(context).show {
                    title(R.string.setasringtone)
                    message(text = content)
                    negativeButton(R.string.cancel)
                    positiveButton(R.string.ok) {
                        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        intent.data = Uri.parse("package:" + context.packageName)
                        context.startActivity(intent)
                    }
                }

                return
            }
        }
        val resolver = context.contentResolver
        val uri: Uri? = getSongFileUri(id)
        try {
            val values = ContentValues(2)
            values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1")
            values.put(MediaStore.Audio.AudioColumns.IS_ALARM, "1")
            uri?.let {
                resolver.update(it, values, null, null)
            }
        } catch (ignored: UnsupportedOperationException) {
            return
        }
        try {
            val cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.MediaColumns.TITLE),
                BaseColumns._ID + "=?", arrayOf(id.toString()),
                null
            )
            cursor.use {
                if (it != null && it.count == 1) {
                    it.moveToFirst()
                    Settings.System.putString(resolver, Settings.System.RINGTONE, uri.toString())
                    val message: CharSequence = Html.fromHtml(
                            context.getString(
                                    R.string.x_has_been_set_as_ringtone,
                                    it.getString(0)
                            )
                    )
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (ignored: SecurityException) {
        }
    }

    private fun getSongFileUri(songId: Long): Uri? {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
    }

    fun deleteTracks(context: Context, songs: List<Song>) {
        val projection = arrayOf(
            BaseColumns._ID, MediaStore.MediaColumns.DATA
        )
        val selection = StringBuilder()
        selection.append(BaseColumns._ID + " IN (")
        for (i in songs.indices) {
            selection.append(songs[i].songId)
            if (i < songs.size - 1) {
                selection.append(",")
            }
        }
        selection.append(")")
        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(),
                null, null
            )
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    cursor.moveToNext()
                }
                context.contentResolver.delete(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    selection.toString(), null
                )
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val name = cursor.getString(1)
                    try { // File.delete can throw a security exception
                        val f = File(name)
                        cursor.moveToNext()
                    } catch (ex: SecurityException) {
                        cursor.moveToNext()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
                cursor.close()
            }
            context.contentResolver.notifyChange(Uri.parse("content://media"), null)
            val f = File(songs[0].path)
            val str = f.absolutePath
            f.delete()
            MediaScannerConnection.scanFile(context, arrayOf(str), null, null)
            Toast.makeText(
                context,
                context.getString(R.string.deleted_x_songs, songs.size),
                Toast.LENGTH_SHORT
            ).show()
        } catch (ignored: SecurityException) {
        }
    }


    fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.window.decorView.rootView.windowToken, 0
        )
    }

    @TargetApi(21)
    fun navigateToAlbum(context: Activity, albumID: Long, albumName: String?) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment: Fragment
        fragment = AlbumDetailFragment.newInstance(albumID, albumName)
        fragment.setEnterTransition(Slide(Gravity.RIGHT))
        fragment.setExitTransition(Slide(Gravity.LEFT))
        transaction.hide(context.supportFragmentManager.findFragmentById(R.id.fragment)!!)
        transaction.add(R.id.fragment, fragment)
        transaction.addToBackStack(null).commit()
    }

    @TargetApi(21)
    fun navigateToArtist(context: Activity, artistID: Long, artistName: String?) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment: Fragment
        fragment = ArtistDetailFragment.newInstance(artistID, artistName)
        fragment.setEnterTransition(Slide(Gravity.RIGHT))
        fragment.setExitTransition(Slide(Gravity.LEFT))
        transaction.hide(context.supportFragmentManager.findFragmentById(R.id.fragment)!!)
        transaction.add(R.id.fragment, fragment)
        transaction.addToBackStack(null).commit()
    }

    @TargetApi(21)
    fun navigateToGenre(context: Context, genreID: Long, genreName: String?) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment: Fragment
        fragment = GenreDetailFragment.newInstance(genreID, genreName)
        fragment.setEnterTransition(Slide(Gravity.RIGHT))
        fragment.setExitTransition(Slide(Gravity.LEFT))
        transaction.hide(context.supportFragmentManager.findFragmentById(R.id.fragment)!!)
        transaction.add(R.id.fragment, fragment)
        transaction.addToBackStack(null).commit()
    }

    @TargetApi(21)
    fun navigateToPlaylist(context: Context, playlistID: Long, playlistName: String?) {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragment: Fragment
        fragment = PlaylistDetailFragment.newInstance(playlistID, playlistName)
        fragment.setEnterTransition(Slide(Gravity.RIGHT))
        fragment.setExitTransition(Slide(Gravity.LEFT))
        transaction.hide(context.supportFragmentManager.findFragmentById(R.id.fragment)!!)
        transaction.add(R.id.fragment, fragment)
        transaction.addToBackStack(null).commit()
    }

    fun getStatusBarColor(color: Int): Int {
        val arrayOfFloat = FloatArray(3)
        Color.colorToHSV(color, arrayOfFloat)
        arrayOfFloat[2] *= 0.9f
        return Color.HSVToColor(arrayOfFloat)
    }

    fun getMostPopulousSwatch(palette: Palette?): Swatch? {
        var mostPopulous: Swatch? = null
        if (palette != null) {
            for (swatch in palette.swatches) {
                if (mostPopulous == null || swatch.population > mostPopulous.population) {
                    mostPopulous = swatch
                }
            }
        }
        return mostPopulous
    }

    fun createStr(
        context: Context, pluralInt: Int,
        number: Int
    ): String? {
        return context.resources.getQuantityString(pluralInt, number, number)
    }

    fun removeSearchHistory(context: Context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().remove("searchquery").apply()
        settings.edit().remove("lastsearch").apply()
        settings.edit().remove("last_single_search").apply()
        Toast.makeText(
            context,
            context.getString(R.string.search_history_removed),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun getSearchQuery(context: Context): String? {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getString("searchquery", "")
    }

    fun setLastSearch(context: Context, query: String?) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putString("lastsearch", query).apply()
    }

    fun getLastSearch(context: Context): String? {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getString("lastsearch", "")
    }

    fun setSearchQuery(context: Context, query: String?) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putString("searchquery", query).apply()
    }

    fun getDuraStr(elapsedSeconds: Long, context: Context): String {
        var elapsedSeconds = elapsedSeconds
        var hours: Long = 0
        var minutes: Long = 0
        var seconds: Long = 0
        if (elapsedSeconds >= 3600) {
            hours = elapsedSeconds / 3600
            elapsedSeconds -= hours * 3600
        }
        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60
            elapsedSeconds -= minutes * 60
        }
        seconds = elapsedSeconds
        var sb: java.lang.StringBuilder? = null
        if (sb == null) {
            sb = java.lang.StringBuilder(8)
        } else {
            sb.setLength(0)
        }
        val f = Formatter(sb, Locale.getDefault())
        initFormatStrings(context)
        return if (hours > 0) {
            f.format(sElapsedFormatHMMSS, hours, minutes, seconds).toString()
        } else {
            f.format(sElapsedFormatMMSS, minutes, seconds).toString()
        }
    }

    private fun initFormatStrings(context: Context) {
        synchronized(sLock) {
            initFormatStringsLocked(
                context
            )
        }
    }

    private fun initFormatStringsLocked(context: Context) {
        val r = Resources.getSystem()
        val cfg = r.configuration
        if (sLastConfig == null || !sLastConfig!!.equals(cfg)) {
            sLastConfig = cfg
            sElapsedFormatMMSS = context.getString(R.string.elapsed_time_short_format_mm_ss)
            sElapsedFormatHMMSS = context.getString(R.string.elapsed_time_short_format_h_mm_ss)
        }
    }

    fun getDuration(durationLong: Long): String {
        val hours = durationLong / 3600
        val minutes = durationLong % 3600 / 60
        val seconds = durationLong % 60
        return if (hours != 0L) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun setServiceDestroy(context: Context, `val`: Boolean) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        settings.edit().putBoolean("app_service_destroy", `val`).apply()
    }

    fun getServiceDestroy(context: Context): Boolean {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE)
        return settings.getBoolean("app_service_destroy", false)
    }

}