package com.wolcano.musicplayer.music.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;

import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEColorPreference;
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEListPreference;
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreferenceFragmentCompat;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.utils.FileUtils;
import com.wolcano.musicplayer.music.utils.PrefUtils;
import com.wolcano.musicplayer.music.utils.Utils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends BaseActivitySettings implements ColorChooserDialog.ColorCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.statusBarCustom)
    StatusBarView statusBarView;
    @BindView(R.id.content_frame)
    FrameLayout contentFrame;
    private int color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setDrawUnderStatusbar(true);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT == 17 || Build.VERSION.SDK_INT==18){
            contentFrame.setPadding(0,30,0,0);
        } else if(Build.VERSION.SDK_INT==16){
            contentFrame.setPadding(15,30,15,0);
        }

        color = Utils.getPrimaryColor(this);
        setStatusbarColor(color, statusBarView);
        if (Build.VERSION.SDK_INT < 21 && findViewById(R.id.statusBarCustom) != null) {
            findViewById(R.id.statusBarCustom).setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(this);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById(R.id.toolbar).getLayoutParams();
                layoutParams.setMargins(0, statusBarHeight, 0, 0);
                findViewById(R.id.toolbar).setLayoutParams(layoutParams);
            }
        }

        setTaskDescriptionColorAuto();

        toolbar.setBackgroundColor(color);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        } else {
            SettingsFragment frag = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (frag != null) frag.invalidateSettings();
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        String tag = dialog.tag();
        if (tag.equals("dialog1")) {
            Utils.setPrimaryColor(this, selectedColor);
            ThemeStore.editTheme(this)
                    .primaryColor(selectedColor)
                    .commit();
        } else if (tag.equals("dialog2")) {
            Utils.setAccentColor(this, selectedColor);
            ThemeStore.editTheme(this)
                    .accentColor(selectedColor)
                    .commit();
        }
        recreate();
        Utils.setColorSelection(this, true);
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(this, toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));
        return super.onCreateOptionsMenu(menu);
    }

    public static class SettingsFragment extends ATEPreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static void setSummary(Preference preference, @NonNull Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                preference.setSummary(stringValue);
            }
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.pref_general);

            addPreferencesFromResource(R.xml.pref_colors);

            addPreferencesFromResource(R.xml.pref_others);


            addPreferencesFromResource(R.xml.pref_others_2);

        }

        @Nullable
        @Override
        public DialogFragment onCreatePreferenceDialog(Preference preference) {
            return super.onCreatePreferenceDialog(preference);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getListView().setPadding(0, 0, 0, 0);
            invalidateSettings();
            PrefUtils.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            PrefUtils.getInstance(getActivity()).unregisterOnSharedPreferenceChangedListener(this);
        }

        private void invalidateSettings() {
            handleGeneralSettings();
            handleColorSettings();
            handleFolderSettings();
            handleOthers();
        }

        private void handleOthers() {
            final com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference rateApp = (com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference) findPreference("rate_app");
            rateApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Utils.rateWolcano(getContext());
                    return true;
                }
            });
            final com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference shareApp = (com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference) findPreference("share_app");
            shareApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Utils.shareWolcano(getContext());
                    return true;
                }
            });
        }

        private void handleGeneralSettings() {
            final ATEListPreference opening = (ATEListPreference) findPreference("opening");
            opening.setValueIndex(Utils.getOpeningVal(getContext()));
            setSummary(opening, Utils.getOpeningVal(getContext()));

            opening.setOnPreferenceChangeListener((preference, o) -> {
                setSummary(opening, o);
                Utils.setOpeningVal(getContext(), Integer.valueOf(o.toString()));

                return true;
            });
            final com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference thefirst = (com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference) findPreference("thefirst");
            thefirst.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String content;
                    if(Build.VERSION.SDK_INT>=23){
                        content = getString(R.string.first_dec);
                    } else {
                        content = getString(R.string.first_dec_old);
                    }
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.pref_title_howtouse)
                            .content(content)
                            .theme(Theme.DARK)
                            .positiveText(R.string.close)
                            .canceledOnTouchOutside(false)
                            .btnStackedGravity(GravityEnum.END)
                            .show();
                    return true;
                }
            });
            final com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEListPreference sleeptimer = (com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEListPreference) findPreference("sleeptimerbehav");
            sleeptimer.setValueIndex(Utils.getOpeningValSleep(getContext()));
            sleeptimer.setOnPreferenceChangeListener((preference, o) -> {
                Utils.setOpeningValSleep(getContext(), Integer.valueOf(o.toString()));
                Toast.makeText(getContext(),getSleepTimerStr(Integer.parseInt(o.toString())),Toast.LENGTH_SHORT).show();
                return true;
            });
        }
        private Spanned getSleepTimerStr(int val){
           if(val==1){
               return Html.fromHtml(getString(R.string.sleeptimer_set_stop));
           }else {
               return Html.fromHtml(getString(R.string.sleeptimer_set_pause));
           }
        }
        private void handleColorSettings() {

            final ATEColorPreference primaryColorPref = (ATEColorPreference) findPreference("primary_color");
            final int primaryColor = Utils.getPrimaryColor(getContext());
            primaryColorPref.setColor(primaryColor, ColorUtil.darkenColor(primaryColor));
            primaryColorPref.setOnPreferenceClickListener(preference -> {

                new ColorChooserDialog.Builder(getContext(), R.string.primary_color_desc_title)
                        .titleSub(R.string.colors)  // title of dialog when viewing shades of a color
                        .doneButton(R.string.done)  // changes label of the done button
                        .cancelButton(R.string.cancel)  // changes label of the cancel button
                        .backButton(R.string.back)  // changes label of the back button
                        .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                        .theme(Theme.DARK)
                        .tag("dialog1")
                        .customButton(R.string.custom)
                        .presetsButton(R.string.presets)

                        .show(getActivity()); // an AppCompatActivity which implements ColorCallback

                return true;
            });

            final ATEColorPreference accentColorPref = (ATEColorPreference) findPreference("accent_color");
            final int accentColor = Utils.getAccentColor(getContext());
            accentColorPref.setColor(accentColor, ColorUtil.darkenColor(accentColor));
            accentColorPref.setOnPreferenceClickListener(preference -> {
                new ColorChooserDialog.Builder(getContext(), R.string.accent_color_desc_title)
                        .titleSub(R.string.colors)  // title of dialog when viewing shades of a color
                        .doneButton(R.string.done)  // changes label of the done button
                        .cancelButton(R.string.cancel)  // changes label of the cancel button
                        .backButton(R.string.back)  // changes label of the back button
                        .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                        .theme(Theme.DARK)
                        .tag("dialog2")
                        .customButton(R.string.custom)
                        .presetsButton(R.string.presets)
                        .show(getActivity()); // an AppCompatActivity which implements ColorCallback


                return true;
            });
        }

        private void handleFolderSettings() {

            final com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference emailUs = (com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference) findPreference("email_us");
            emailUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.pref_title_email_us)
                            .content(R.string.pref_email_us_dialog_content)
                            .negativeText(R.string.no)
                            .positiveText(R.string.yes)
                            .positiveColor(Utils.getAccentColor(getContext()))
                            .negativeColor(Utils.getAccentColor(getContext()))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                    intent.setData(Uri.parse("mailto:"));
                                    intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { "wolcanoapps@protonmail.com" });
                                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_us_title));

                                    startActivity(Intent.createChooser(intent, getString(R.string.email_us_via)));
                                }
                            })
                            .theme(Theme.DARK)
                            .show();
                    return true;
                }
            });

            final com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference deleteHistory = (com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference) findPreference("delete_history");
            deleteHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.pref_title_delete_history)
                            .content(R.string.pref_delete_history_dialog_content)
                            .negativeText(R.string.no)
                            .positiveText(R.string.yes)
                            .positiveColor(Utils.getAccentColor(getContext()))
                            .negativeColor(Utils.getAccentColor(getContext()))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Utils.removeSearchHistory(getContext());
                                }
                            })
                            .theme(Theme.DARK)
                            .show();
                    return true;
                }
            });
            final com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference backupHistory = (com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference) findPreference("backup_history");
            backupHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    new MaterialDialog.Builder(getContext())
                            .title(R.string.pref_title_backup_history)
                            .content(R.string.pref_backup_history_dialog_content)
                            .negativeText(R.string.no)
                            .positiveText(R.string.yes)
                            .positiveColor(Utils.getAccentColor(getContext()))
                            .negativeColor(Utils.getAccentColor(getContext()))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    boolean isSuccessful = false;
                                    if (FileUtils.writeToFile(getContext(), Utils.getSearchQuery(getContext()), getContext().getString(R.string.backup_search_history_file_name))) {
                                        isSuccessful = true;
                                    } else {
                                        isSuccessful = false;
                                    }
                                    if (FileUtils.writeToFile(getContext(), Utils.getLastSearch(getContext()), getString(R.string.backup_last_searches_file_name))) {
                                        isSuccessful = true;
                                    } else {
                                        isSuccessful = false;

                                    }
                                    if (isSuccessful) {
                                        Toast.makeText(getContext(), getContext().getString(R.string.backup_success), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), getContext().getString(R.string.backup_fail), Toast.LENGTH_SHORT).show();

                                    }


                                }
                            })
                            .theme(Theme.DARK)
                            .show();
                    return true;
                }
            });
            final com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference importHistory = (com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference) findPreference("import_history");
            importHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.pref_title_import_history)
                            .content(R.string.pref_backup_import_dialog_content)
                            .negativeText(R.string.no)
                            .positiveText(R.string.yes)
                            .positiveColor(Utils.getAccentColor(getContext()))
                            .negativeColor(Utils.getAccentColor(getContext()))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    String directoryPath1 =
                                            Environment.getExternalStorageDirectory()
                                                    + File.separator
                                                    + getContext().getString(R.string.folder_name) + File.separator + getContext().getString(R.string.folder_search_history)
                                                    + File.separator
                                                    + getContext().getString(R.string.backup_search_history_file_name) + ".txt";
                                    String directoryPath2 =
                                            Environment.getExternalStorageDirectory()
                                                    + File.separator
                                                    + getContext().getString(R.string.folder_name) + File.separator + getContext().getString(R.string.folder_search_history)
                                                    + File.separator
                                                    + getContext().getString(R.string.backup_last_searches_file_name) + ".txt";

                                    try {

                                        FileUtils.readFileData(getContext(), directoryPath1, directoryPath2);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            })
                            .theme(Theme.DARK)
                            .show();
                    return true;
                }
            });
            final androidx.preference.TwoStatePreference autoSearch = (TwoStatePreference) findPreference("remember_last_search");
            autoSearch.setChecked(Utils.getAutoSearch(getContext()));
            autoSearch.setOnPreferenceChangeListener((preference, newValue) -> {
                Utils.setAutoSearch(getContext(), (boolean) newValue);
                return true;
            });

        }



        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }

}
