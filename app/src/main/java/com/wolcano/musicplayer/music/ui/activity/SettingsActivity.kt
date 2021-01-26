package com.wolcano.musicplayer.music.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.text.Spanned
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.afollestad.materialdialogs.color.ColorChooserDialog.ColorCallback
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEColorPreference
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEListPreference
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreference
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreferenceFragmentCompat
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.ActivitySettingsBinding
import com.wolcano.musicplayer.music.ui.activity.base.BaseActivitySettings
import com.wolcano.musicplayer.music.utils.FileUtils
import com.wolcano.musicplayer.music.utils.PrefUtils
import com.wolcano.musicplayer.music.utils.Utils
import java.io.File
import java.io.IOException

class SettingsActivity: BaseActivitySettings(), ColorCallback {

    private var color = 0

    private val binding: ActivitySettingsBinding by binding(R.layout.activity_settings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDrawUnderStatusbar(true)

        color = Utils.getPrimaryColor(this)
        setStatusbarColor(color, binding.statusBarCustom)

        setTaskDescriptionColorAuto()
        binding.toolbar.setBackgroundColor(color)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SettingsFragment()).commit()
        } else {
            val frag: SettingsFragment? =
                supportFragmentManager.findFragmentById(R.id.content_frame) as SettingsFragment?
            frag?.invalidateSettings()
        }
    }


    override fun onColorSelection(dialog: ColorChooserDialog, @ColorInt selectedColor: Int) {
        val tag = dialog.tag()
        if (tag == "dialog1") {
            Utils.setPrimaryColor(this, selectedColor)
            ThemeStore.editTheme(this)
                .primaryColor(selectedColor)
                .commit()
        } else if (tag == "dialog2") {
            Utils.setAccentColor(this, selectedColor)
            ThemeStore.editTheme(this)
                .accentColor(selectedColor)
                .commit()
        }
        recreate()
        Utils.setColorSelection(this, true)
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            this,
            binding.toolbar,
            menu,
            ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar)
        )
        return super.onCreateOptionsMenu(menu)
    }


    class SettingsFragment : ATEPreferenceFragmentCompat(),
        OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            addPreferencesFromResource(R.xml.pref_general)
            addPreferencesFromResource(R.xml.pref_colors)
            addPreferencesFromResource(R.xml.pref_online)
            addPreferencesFromResource(R.xml.pref_others)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            listView.setPadding(0, 0, 0, 0)
            invalidateSettings()
            PrefUtils.getInstance(activity!!).registerOnSharedPreferenceChangedListener(this)
        }

        override fun onDestroyView() {
            super.onDestroyView()
            PrefUtils.getInstance(activity!!).unregisterOnSharedPreferenceChangedListener(this)
        }

        fun invalidateSettings() {
            handleGeneralSettings()
            handleColorSettings()
            handleOnlineSettings()
            handleOthers()
        }

        private fun handleOthers() {
            val rateApp = findPreference("rate_app") as ATEPreference
            rateApp.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Utils.rateWolcano(context)
                true
            }
            val shareApp = findPreference("share_app") as ATEPreference
            shareApp.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Utils.shareWolcano(context)
                true
            }
        }

        private fun handleGeneralSettings() {
            val opening = findPreference("opening") as ATEListPreference
            opening.setValueIndex(Utils.getOpeningVal(context))
            setSummary(
                opening, Utils.getOpeningVal(
                    context
                )
            )
            opening.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference: Preference?, o: Any ->
                    setSummary(opening, o)
                    Utils.setOpeningVal(
                        context,
                        Integer.valueOf(o.toString())
                    )
                    true
                }
            val howtouse = findPreference("howtouse") as ATEPreference
            howtouse.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val content: String
                content = if (Build.VERSION.SDK_INT >= 23) {
                    getString(R.string.first_dec)
                } else {
                    getString(R.string.first_dec_old)
                }
                MaterialDialog.Builder(context!!)
                    .title(R.string.pref_title_howtouse)
                    .content(content)
                    .theme(Theme.DARK)
                    .positiveText(R.string.close)
                    .canceledOnTouchOutside(false)
                    .btnStackedGravity(GravityEnum.END)
                    .show()
                true
            }
            val sleeptimer = findPreference("sleeptimerbehav") as ATEListPreference
            sleeptimer.setValueIndex(
                Utils.getOpeningValSleep(
                    context
                )
            )
            sleeptimer.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference: Preference?, o: Any ->
                    Utils.setOpeningValSleep(
                        context,
                        Integer.valueOf(o.toString())
                    )
                    Toast.makeText(
                        context,
                        getSleepTimerStr(o.toString().toInt()),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    true
                }
        }

        private fun getSleepTimerStr(`val`: Int): Spanned {
            return if (`val` == 1) {
                Html.fromHtml(getString(R.string.sleeptimer_set_stop))
            } else {
                Html.fromHtml(getString(R.string.sleeptimer_set_pause))
            }
        }

        private fun handleColorSettings() {
            val primaryColorPref = findPreference("primary_color") as ATEColorPreference
            val primaryColor = Utils.getPrimaryColor(context)
            primaryColorPref.setColor(primaryColor, ColorUtil.darkenColor(primaryColor))
            primaryColorPref.onPreferenceClickListener =
                Preference.OnPreferenceClickListener { preference: Preference? ->
                    ColorChooserDialog.Builder(
                        context!!, R.string.primary_color_desc_title
                    )
                        .titleSub(R.string.colors) // title of dialog when viewing shades of a color
                        .doneButton(R.string.done) // changes label of the done button
                        .cancelButton(R.string.cancel) // changes label of the cancel button
                        .backButton(R.string.back) // changes label of the back button
                        .dynamicButtonColor(true) // defaults to true, false will disable changing action buttons' color to currently selected color
                        .theme(Theme.DARK)
                        .tag("dialog1")
                        .customButton(R.string.custom)
                        .presetsButton(R.string.presets)
                        .show(activity) // an AppCompatActivity which implements ColorCallback
                    true
                }
            val accentColorPref = findPreference("accent_color") as ATEColorPreference
            val accentColor = Utils.getAccentColor(context)
            accentColorPref.setColor(accentColor, ColorUtil.darkenColor(accentColor))
            accentColorPref.onPreferenceClickListener =
                Preference.OnPreferenceClickListener { preference: Preference? ->
                    ColorChooserDialog.Builder(
                        context!!, R.string.accent_color_desc_title
                    )
                        .titleSub(R.string.colors) // title of dialog when viewing shades of a color
                        .doneButton(R.string.done) // changes label of the done button
                        .cancelButton(R.string.cancel) // changes label of the cancel button
                        .backButton(R.string.back) // changes label of the back button
                        .dynamicButtonColor(true) // defaults to true, false will disable changing action buttons' color to currently selected color
                        .theme(Theme.DARK)
                        .tag("dialog2")
                        .customButton(R.string.custom)
                        .presetsButton(R.string.presets)
                        .show(activity) // an AppCompatActivity which implements ColorCallback
                    true
                }
        }

        private fun handleOnlineSettings() {
            val emailUs = findPreference("email_us") as ATEPreference
            emailUs.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                MaterialDialog.Builder(context!!)
                    .title(R.string.pref_title_email_us)
                    .content(R.string.pref_email_us_dialog_content)
                    .negativeText(R.string.no)
                    .positiveText(R.string.yes)
                    .positiveColor(Utils.getAccentColor(context))
                    .negativeColor(Utils.getAccentColor(context))
                    .onPositive { dialog, which ->
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.data = Uri.parse("mailto:")
                        intent.putExtra(
                            Intent.EXTRA_EMAIL,
                            arrayOf("wolcanoapps@protonmail.com")
                        )
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_us_title))
                        startActivity(
                            Intent.createChooser(
                                intent,
                                getString(R.string.email_us_via)
                            )
                        )
                    }
                    .theme(Theme.DARK)
                    .show()
                true
            }
            val deleteHistory = findPreference("delete_history") as ATEPreference
            deleteHistory.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                MaterialDialog.Builder(context!!)
                    .title(R.string.pref_title_delete_history)
                    .content(R.string.pref_delete_history_dialog_content)
                    .negativeText(R.string.no)
                    .positiveText(R.string.yes)
                    .positiveColor(Utils.getAccentColor(context))
                    .negativeColor(Utils.getAccentColor(context))
                    .onPositive { dialog, which ->
                        Utils.removeSearchHistory(
                            context
                        )
                    }
                    .theme(Theme.DARK)
                    .show()
                true
            }
            val backupHistory = findPreference("backup_history") as ATEPreference
            backupHistory.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                MaterialDialog.Builder(context!!)
                    .title(R.string.pref_title_backup_history)
                    .content(R.string.pref_backup_history_dialog_content)
                    .negativeText(R.string.no)
                    .positiveText(R.string.yes)
                    .positiveColor(Utils.getAccentColor(context))
                    .negativeColor(Utils.getAccentColor(context))
                    .onPositive { dialog, which ->
                        var isSuccessful = false
                        isSuccessful = if (FileUtils.writeToFile(
                                context, Utils.getSearchQuery(
                                    context
                                ), context!!.getString(R.string.backup_search_history_file_name)
                            )
                        ) {
                            true
                        } else {
                            false
                        }
                        isSuccessful = if (FileUtils.writeToFile(
                                context, Utils.getLastSearch(
                                    context
                                ), getString(R.string.backup_last_searches_file_name)
                            )
                        ) {
                            true
                        } else {
                            false
                        }
                        if (isSuccessful) {
                            Toast.makeText(
                                context,
                                context!!.getString(R.string.backup_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                context!!.getString(R.string.backup_fail),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .theme(Theme.DARK)
                    .show()
                true
            }
            val importHistory = findPreference("import_history") as ATEPreference
            importHistory.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                MaterialDialog.Builder(context!!)
                    .title(R.string.pref_title_import_history)
                    .content(R.string.pref_backup_import_dialog_content)
                    .negativeText(R.string.no)
                    .positiveText(R.string.yes)
                    .positiveColor(Utils.getAccentColor(context))
                    .negativeColor(Utils.getAccentColor(context))
                    .onPositive { dialog, which ->
                        val directoryPath1 = (Environment.getExternalStorageDirectory()
                            .toString() + File.separator
                                + context!!.getString(R.string.folder_name) + File.separator + context!!.getString(
                            R.string.folder_search_history
                        )
                                + File.separator
                                + context!!.getString(R.string.backup_search_history_file_name) + ".txt")
                        val directoryPath2 = (Environment.getExternalStorageDirectory()
                            .toString() + File.separator
                                + context!!.getString(R.string.folder_name) + File.separator + context!!.getString(
                            R.string.folder_search_history
                        )
                                + File.separator
                                + context!!.getString(R.string.backup_last_searches_file_name) + ".txt")
                        try {
                            FileUtils.readFileData(
                                context,
                                directoryPath1,
                                directoryPath2
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    .theme(Theme.DARK)
                    .show()
                true
            }
            val autoSearch = findPreference("remember_last_search") as TwoStatePreference
            autoSearch.isChecked = Utils.getAutoSearch(context)
            autoSearch.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference: Preference?, newValue: Any ->
                    Utils.setAutoSearch(
                        context,
                        newValue as Boolean
                    )
                    true
                }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {}

        companion object {
            private fun setSummary(preference: Preference, value: Any) {
                val stringValue = value.toString()
                if (preference is ListPreference) {
                    val index = preference.findIndexOfValue(stringValue)
                    preference.setSummary(
                        if (index >= 0) preference.entries[index] else null
                    )
                } else {
                    preference.summary = stringValue
                }
            }
        }
    }

}