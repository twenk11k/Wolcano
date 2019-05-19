package com.wolcano.musicplayer.music.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.wolcano.musicplayer.music.App;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.detail.AlbumDetailFragment;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.detail.ArtistDetailFragment;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.detail.GenreDetailFragment;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.detail.PlaylistDetailFragment;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static com.afollestad.materialdialogs.color.CircleView.shiftColor;

public class Utils {

    private static SharedPreferences settings;
    private static Configuration sLastConfig;
    private static String sElapsedFormatMMSS;
    private static String sElapsedFormatHMMSS;
    private static final Object sLock = new Object();


    public static int getPrimaryColor(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        int color = settings.getInt("color_theme", 0);
        if (color == 0) {
            color = context.getResources().getColor(R.color.colorPrimary);
        }
        return color;

    }

    public static void setPrimaryColor(Context context, int color) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putInt("color_theme", color).apply();

    }

    public static void setAccentColor(Context context, int color) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putInt("color_accent", color).apply();

    }

    public static int getAccentColor(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        int color = settings.getInt("color_accent", 0);
        if (color == 0) {
            color = context.getResources().getColor(R.color.colorAccent);
        }
        return color;

    }

    public static int getOpeningVal(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getInt("app_opening", 0);

    }
    public static void setOpeningVal(Context context, int pos) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putInt("app_opening", pos).apply();

    }
    public static int getOpeningVal2(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getInt("app_opening_2", 4);

    }
    public static void setOpeningVal2(Context context, int pos) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putInt("app_opening_2", pos).apply();

    }
    public static boolean getMobileInteret(Context context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getBoolean("app_mobile_interet", false);
    }

    public static void setMobileInteret(Context context,boolean val) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putBoolean("app_mobile_interet", val).apply();
    }


    public static boolean getIsMopubInitDone(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getBoolean("app_is_mopub_init_done", false);

    }
    public static void setIsMopubInitDone(Context context, boolean val) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putBoolean("app_is_mopub_init_done", val).apply();

    }
    public static int getPlaylistPos(Context context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getInt("app_playlist_pos", 0);

    }

    public static void savePlayPosition(Context context,int pos) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putInt("app_playlist_pos", pos).apply();

    }

    public static int getOpeningValSleep(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getInt("app_opening_sleep", 0);

    }

    public static void setOpeningValSleep(Context context, int pos) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putInt("app_opening_sleep", pos).apply();

    }
    public static void setFirst(Context context, boolean val) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putBoolean("app_howtouse", val).apply();

    }
    public static boolean getFirst(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getBoolean("app_howtouse", true);

    }

    public static boolean getAutoSearch(Context context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getBoolean("app_auto_search", false);

    }

    public static void setAutoSearch(Context context, boolean pos) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putBoolean("app_auto_search", pos).apply();

    }
    public static boolean getColorSelection(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        boolean bol = false;
        return settings.getBoolean("app_color_selection", bol);

    }

    public static void setColorSelection(Context context, boolean val) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putBoolean("app_color_selection", val).apply();

    }
    public static int getPlaylistId(Context context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getInt("app_playlist_id", 0);
    }

    public static void setPlaylistId(Context context,int val) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putInt("app_playlist_id", val).apply();
    }
    public static boolean getRecreated(Context context) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getBoolean("recreated_val", false);

    }

    public static void setRecreated(Context context, boolean val) {

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putBoolean("recreated_val", val).apply();

    }
    public static int getCountSave(Context context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        return settings.getInt("count_save", 0);
    }

    public static void setCountSave(Context context, int pos) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        settings.edit().putInt("count_save", pos).apply();
    }
    public static int getLastSleepTimerValue(Context context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        return settings.getInt("last_sleep_timer_value", 30);
    }

    public static void setLastSleepTimerValue(Context context,final int value) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putInt("last_sleep_timer_value", value).apply();
    }
    public static long getNextSleepTimerElapsedRealTime(Context context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        return settings.getLong("next_sleep_timer_elapsed_real_time", -1);
    }
    public static void setFirstFrag(Context context,final boolean val){
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putBoolean("app_first_frag", val).apply();
    }
    public static boolean getFirstFrag(Context context) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        return settings.getBoolean("app_first_frag", false);
    }
    public static void setNextSleepTimerElapsedRealtime(Context context,final long value) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().putLong("next_sleep_timer_elapsed_real_time", value).apply();
    }

    public static int getStatHeight(Context context) {
        int height = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            height = context.getResources().getDimensionPixelSize(resId);
        }
        return height;
    }
    public static int getDeviceScrWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }
    @ColorInt
    public static int darkenColor(@ColorInt int color) {
        return shiftColor(color, 0.9F);
    }

    public static boolean isColorLight(@ColorInt int color) {
        double darkness = 1.0D - (0.299D * (double) Color.red(color) + 0.587D * (double) Color.green(color) + 0.114D * (double) Color.blue(color)) / 255.0D;
        return darkness < 0.4D;
    }

    private static void setLightStatusbar2(Activity activity, boolean enabled) {
        if (Build.VERSION.SDK_INT >= 23) {
            View decorView = activity.getWindow().getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            if (enabled) {
                decorView.setSystemUiVisibility(systemUiVisibility | 8192);
            } else {
                decorView.setSystemUiVisibility(systemUiVisibility & -8193);
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setStatusBarTranslucent(@NonNull Window window) {
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static void setStatusbarColorAuto(StatusBarView statusBarView, int color, Activity activity) {
        setStatusbarColor(color, statusBarView, activity);
    }

    private static void setStatusbarColor(int color, StatusBarView statusBarView, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (statusBarView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    statusBarView.setBackgroundColor(Utils.darkenColor(color));
                    setLightStatusbarAuto(activity, color);
                } else {
                    statusBarView.setBackgroundColor(color);
                }
            }
        }
    }

    private static void setLightStatusbarAuto(Activity activity, int bgColor) {
        setLightStatusbar(activity, Utils.isColorLight(bgColor));
    }

    private static void setLightStatusbar(Activity activity, boolean enabled) {
        setLightStatusbar2(activity, enabled);
    }

    public static void rateWolcano(Context context) {
        Uri url = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                );
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }

    }

    public static void shareWolcano(Context context) {
        int appId = context.getApplicationInfo().labelRes;
        final String pckg = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(appId));
        String appName = context.getResources().getString(R.string.appname);
        String store = "https://play.google.com/store/apps/details?id=" + pckg;
        intent.putExtra(Intent.EXTRA_TEXT, appName + " " + store);
        String str = context.getResources().getString(R.string.sharewolcano);
        context.startActivity(Intent.createChooser(intent, str));
    }

    public static void setAllowDrawUnderStatusBar(@NonNull Window window) {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setUpFastScrollRecyclerViewColor(FastScrollRecyclerView recyclerView, int accentColor) {
        recyclerView.setPopupBgColor(accentColor);
        recyclerView.setThumbColor(accentColor);
        recyclerView.setTrackColor(Color.TRANSPARENT);
        if (ColorUtil.isColorLight(accentColor)) {
            recyclerView.setPopupTextColor(Color.BLACK);
        } else {
            recyclerView.setPopupTextColor(Color.WHITE);
        }
    }

    public static void setRingtone(@NonNull final Context context, final long id) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                CharSequence content;
                content = Html.fromHtml(context.getString(R.string.set_ringtone_message_first,context.getString(R.string.appname)));
                new MaterialDialog.Builder(context)
                        .title(R.string.setasringtone)
                        .content(content)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .positiveColor(Utils.getAccentColor(context))
                        .negativeColor(Utils.getAccentColor(context))
                        .theme(Theme.DARK)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);
                            }
                        })
                        .build().show();
                return;
            }
        }
        final ContentResolver resolver = context.getContentResolver();
        final Uri uri = getSongFileUri(id);
        try {
            final ContentValues values = new ContentValues(2);
            values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1");
            values.put(MediaStore.Audio.AudioColumns.IS_ALARM, "1");
            resolver.update(uri, values, null, null);
        } catch (@NonNull final UnsupportedOperationException ignored) {
            return;
        }

        try {
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.MediaColumns.TITLE},
                    BaseColumns._ID + "=?",
                    new String[]{String.valueOf(id)},
                    null);
            try {
                if (cursor != null && cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    Settings.System.putString(resolver, Settings.System.RINGTONE, uri.toString());
                    CharSequence message = Html.fromHtml(context.getString(R.string.x_has_been_set_as_ringtone,cursor.getString(0)));
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (SecurityException ignored) {
        }
    }

    private static Uri getSongFileUri(long songId) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }

    public static void deleteTracks(@NonNull final Context context, @NonNull final List<Song> songs) {
        final String[] projection = new String[]{
                BaseColumns._ID, MediaStore.MediaColumns.DATA
        };
        final StringBuilder selection = new StringBuilder();
        selection.append(BaseColumns._ID + " IN (");
        for (int i = 0; i < songs.size(); i++) {
            selection.append(songs.get(i).getSongId());
            if (i < songs.size() - 1) {
                selection.append(",");
            }
        }
        selection.append(")");

        try {
            final Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(),
                    null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    cursor.moveToNext();
                }

                context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        selection.toString(), null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    final String name = cursor.getString(1);
                    try { // File.delete can throw a security exception
                        final File f = new File(name);

                        cursor.moveToNext();
                    } catch (@NonNull final SecurityException ex) {
                        cursor.moveToNext();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
            File f = new File(songs.get(0).getPath());
            String str = f.getAbsolutePath();
            f.delete();
            MediaScannerConnection.scanFile(context, new String[]{str}, null, null);

            Toast.makeText(context, context.getString(R.string.deleted_x_songs, songs.size()), Toast.LENGTH_SHORT).show();
        } catch (SecurityException ignored) {
        }
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(21)
    public static void navigateToAlbum(Activity context, long albumID, String albumName) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        fragment = AlbumDetailFragment.newInstance(albumID, albumName   );

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment));
        transaction.add(R.id.fragment, fragment);

        transaction.addToBackStack(null).commit();

    }
    @TargetApi(21)
    public static void navigateToArtist(Activity context, long artistID, String artistName) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        fragment = ArtistDetailFragment.newInstance(artistID, artistName);

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment));
        transaction.add(R.id.fragment, fragment);
        transaction.addToBackStack(null).commit();

    }
    @TargetApi(21)
    public static void navigateToGenre(Context context, long genreID, String genreName) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        fragment = GenreDetailFragment.newInstance(genreID, genreName);

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment));
        transaction.add(R.id.fragment, fragment);
        transaction.addToBackStack(null).commit();

    }
    @TargetApi(21)
    public static void navigateToPlaylist(Context context, long playlistID, String playlistName) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        fragment = PlaylistDetailFragment.newInstance(playlistID, playlistName);

        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment));
        transaction.add(R.id.fragment, fragment);
        transaction.addToBackStack(null).commit();

    }
    public static int getStatusBarColor(int color) {
        float[] arrayOfFloat = new float[3];
        Color.colorToHSV(color, arrayOfFloat);
        arrayOfFloat[2] *= 0.9F;
        return Color.HSVToColor(arrayOfFloat);
    }
    public static @Nullable
    Palette.Swatch getMostPopulousSwatch(Palette palette) {
        Palette.Swatch mostPopulous = null;
        if (palette != null) {
            for (Palette.Swatch swatch : palette.getSwatches()) {
                if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                    mostPopulous = swatch;
                }
            }
        }
        return mostPopulous;
    }
    public static String createStr(final Context context, final int pluralInt,
                                         final int number) {
        return context.getResources().getQuantityString(pluralInt, number, number);
    }
    public static void removeSearchHistory(Context context){
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        settings.edit().remove("searchquery").apply();
        settings.edit().remove("lastsearch").apply();
        settings.edit().remove("last_single_search").apply();
        Toast.makeText(context,context.getString(R.string.search_history_removed), Toast.LENGTH_SHORT).show();
    }
    public static String getSearchQuery(Context context){
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getString("searchquery", "");
    }
    public static String getLastSearch(Context context){
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getString("lastsearch", "");
    }
    public static void setSearchQuery(Context context, String query) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        settings.edit().putString("searchquery", query).apply();
    }
    public static void setGetSearch(Context context, String query) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        settings.edit().putString("getsearch", query).apply();
    }
    public static void setLastSearch(Context context, String query) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        settings.edit().putString("lastsearch", query).apply();
    }

    public static void setLastSingleSearch(Context context, String query) {
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        settings.edit().putString("last_single_search", query).apply();
    }
    public static String getLastSingleSearch(Context context){
        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        return settings.getString("last_single_search", "");
    }
        public static String getDuraStr(long elapsedSeconds, Context context){
            long hours = 0;
            long minutes = 0;
            long seconds = 0;
            if (elapsedSeconds >= 3600) {
                hours = elapsedSeconds / 3600;
                elapsedSeconds -= hours * 3600;
            }
            if (elapsedSeconds >= 60) {
                minutes = elapsedSeconds / 60;
                elapsedSeconds -= minutes * 60;
            }
            seconds = elapsedSeconds;
            StringBuilder sb = null;
            if (sb == null) {
                sb = new StringBuilder(8);
            } else {
                sb.setLength(0);
            }
            Formatter f = new Formatter(sb, Locale.getDefault());
            initFormatStrings(context);
            if (hours > 0) {
                return f.format(sElapsedFormatHMMSS, hours, minutes, seconds).toString();
            } else {
                return f.format(sElapsedFormatMMSS, minutes, seconds).toString();
            }
        }
        public static String getReadableDura(long millis){
            long minutes = (millis / 1000) / 60;
            long seconds = (millis / 1000) % 60;
            if (minutes < 60) {
                return String.format("%01d:%02d", minutes, seconds);
            } else {
                long hours = minutes / 60;
                minutes = minutes % 60;
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            }
        }
    private static void initFormatStrings(Context context) {
        synchronized (sLock) {
            initFormatStringsLocked(context);
        }
    }
    private static void initFormatStringsLocked(Context context) {
        Resources r = Resources.getSystem();
        Configuration cfg = r.getConfiguration();
        if (sLastConfig == null || !sLastConfig.equals(cfg)) {
            sLastConfig = cfg;
            sElapsedFormatMMSS = context.getString(R.string.elapsed_time_short_format_mm_ss);
            sElapsedFormatHMMSS = context.getString(R.string.elapsed_time_short_format_h_mm_ss);
        }
    }
    public static boolean isItTrue(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        if(countryCode.equals("jp")){
            return false;
        } else {
            return true;
        }
    }
    public static String getDura(long durationLong) {
        long hours = durationLong / 3600;
        long minutes = (durationLong % 3600) / 60;
        long seconds = durationLong % 60;

        String duration = "";
        if (hours != 0) {
            duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            duration = String.format("%02d:%02d", minutes, seconds);
        }

        return duration;
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(App.dens * value);
    }





}
