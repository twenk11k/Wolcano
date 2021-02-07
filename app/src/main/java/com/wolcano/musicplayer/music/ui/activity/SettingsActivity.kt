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
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.TwoStatePreference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.ActivitySettingsBinding
import com.wolcano.musicplayer.music.prefs.ColorPreference
import com.wolcano.musicplayer.music.ui.activity.base.BaseActivitySettings
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.FileUtils
import com.wolcano.musicplayer.music.utils.PrefUtils
import com.wolcano.musicplayer.music.utils.Utils
import com.wolcano.musicplayer.music.utils.Utils.getPrimaryColor
import java.io.File
import java.io.IOException

class SettingsActivity : BaseActivitySettings() {

    private var color = 0

    private val binding: ActivitySettingsBinding by binding(R.layout.activity_settings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDrawUnderStatusbar(true)

        color = getPrimaryColor(this)
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
            ColorUtils.getToolbarBackgroundColor(binding.toolbar)
        )
        return super.onCreateOptionsMenu(menu)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
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
            PrefUtils(requireActivity()).registerOnSharedPreferenceChangedListener(this)
        }

        override fun onDestroyView() {
            super.onDestroyView()
            PrefUtils(requireActivity())
                .unregisterOnSharedPreferenceChangedListener(this)
        }

        fun invalidateSettings() {
            handleGeneralSettings()
            handleColorSettings()
            handleOnlineSettings()
            handleOthers()
        }

        private fun handleOthers() {
            val rateApp = findPreference("rate_app") as Preference
            rateApp.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Utils.rateWolcano(requireContext())
                true
            }
            val shareApp = findPreference("share_app") as Preference
            shareApp.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Utils.shareWolcano(requireContext())
                true
            }
        }

        private fun handleGeneralSettings() {
            val opening = findPreference("opening") as ListPreference
            opening.setValueIndex(Utils.getOpeningVal(requireContext()))
            setSummary(
                opening, Utils.getOpeningVal(
                    requireContext()
                )
            )
            opening.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, o: Any ->
                    setSummary(opening, o)
                    Utils.setOpeningVal(
                        requireContext(),
                        Integer.valueOf(o.toString())
                    )
                    true
                }
            val howtouse = findPreference("howtouse") as Preference
            howtouse.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val content: String = if (Build.VERSION.SDK_INT >= 23) {
                    getString(R.string.first_dec)
                } else {
                    getString(R.string.first_dec_old)
                }
                MaterialDialog(requireActivity()).show {
                    title(R.string.pref_title_howtouse)
                    message(text = content)
                    positiveButton(R.string.close)
                    cancelOnTouchOutside(false)
                }
                true
            }
            val sleeptimer = findPreference("sleeptimerbehav") as ListPreference
            sleeptimer.setValueIndex(
                Utils.getOpeningValSleep(
                    requireContext()
                )
            )
            sleeptimer.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, o: Any ->
                    Utils.setOpeningValSleep(
                        requireContext(),
                        Integer.valueOf(o.toString())
                    )
                    Toast.makeText(
                        requireContext(),
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
            val primaryColorPref = findPreference("primary_color") as ColorPreference
            val primaryColor = getPrimaryColor(requireContext())
            primaryColorPref.setColor(primaryColor, ColorUtils.darkenColor(primaryColor))
            primaryColorPref.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    MaterialDialog(requireActivity()).show {
                        title(R.string.primary_color_desc_title)
                        colorChooser(
                            colors = ColorPalette.Primary,
                            subColors = ColorPalette.PrimarySub
                        ) { _, color ->
                            Utils.setPrimaryColor(requireContext(), color)
                            requireActivity().recreate()
                            Utils.setColorSelection(requireContext(), true)
                        }
                    }
                    true
                }
            val accentColorPref = findPreference("accent_color") as ColorPreference
            val accentColor = Utils.getAccentColor(requireContext())
            accentColorPref.setColor(accentColor, ColorUtils.darkenColor(accentColor))
            accentColorPref.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    MaterialDialog(requireActivity()).show {
                        title(R.string.accent_color_desc_title)
                        colorChooser(
                            colors = ColorPalette.Accent,
                            subColors = ColorPalette.AccentSub
                        ) { _, color ->
                            Utils.setAccentColor(requireContext(), color)
                            requireActivity().recreate()
                            Utils.setColorSelection(requireContext(), true)
                        }
                    }
                    true
                }
        }

        private fun handleOnlineSettings() {
            val emailUs = findPreference("email_us") as Preference
            emailUs.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                MaterialDialog(requireActivity()).show {
                    title(R.string.pref_title_email_us)
                    message(R.string.pref_email_us_dialog_content)
                        .negativeButton(R.string.no)
                        .positiveButton(R.string.yes) {
                            val intent = Intent(Intent.ACTION_SENDTO)
                            intent.data = Uri.parse("mailto:")
                            intent.putExtra(
                                Intent.EXTRA_EMAIL,
                                arrayOf("wolcanoapps@protonmail.com")
                            )
                            intent.putExtra(
                                Intent.EXTRA_SUBJECT,
                                getString(R.string.email_us_title)
                            )
                            startActivity(
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.email_us_via)
                                )
                            )
                        }
                }
                true
            }
            val deleteHistory = findPreference("delete_history") as Preference
            deleteHistory.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                MaterialDialog(requireActivity()).show {
                    title(R.string.pref_title_delete_history)
                    message(R.string.pref_delete_history_dialog_content)
                    negativeButton(R.string.no)
                    positiveButton(R.string.yes) {
                        Utils.removeSearchHistory(
                            requireContext()
                        )
                    }
                }
                true
            }
            val backupHistory = findPreference("backup_history") as Preference
            backupHistory.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                MaterialDialog(requireActivity()).show {
                    title(R.string.pref_title_backup_history)
                    message(R.string.pref_backup_history_dialog_content)
                    negativeButton(R.string.no)
                    positiveButton(R.string.yes) {
                        var isSuccessful = false
                        isSuccessful = if (FileUtils.writeToFile(
                                requireContext(),
                                Utils.getSearchQuery(
                                    requireContext()
                                ),
                                requireContext().getString(R.string.backup_search_history_file_name)
                            )
                        ) {
                            true
                        } else {
                            false
                        }
                        isSuccessful = if (FileUtils.writeToFile(
                                requireContext(), Utils.getLastSearch(
                                    requireContext()
                                ), getString(R.string.backup_last_searches_file_name)
                            )
                        ) {
                            true
                        } else {
                            false
                        }
                        if (isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                requireContext().getString(R.string.backup_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                requireContext().getString(R.string.backup_fail),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                true
            }
            val importHistory = findPreference("import_history") as Preference
            importHistory.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                MaterialDialog(requireActivity()).show {
                    title(R.string.pref_title_import_history)
                    message(R.string.pref_backup_import_dialog_content)
                    negativeButton(R.string.no)
                    positiveButton(R.string.yes) {
                        val directoryPath1 = (Environment.getExternalStorageDirectory()
                            .toString() + File.separator
                                + requireContext().getString(R.string.folder_name) + File.separator + requireContext().getString(
                            R.string.folder_search_history
                        )
                                + File.separator
                                + requireContext().getString(R.string.backup_search_history_file_name) + ".txt")
                        val directoryPath2 = (Environment.getExternalStorageDirectory()
                            .toString() + File.separator
                                + requireContext().getString(R.string.folder_name) + File.separator + requireContext().getString(
                            R.string.folder_search_history
                        )
                                + File.separator
                                + requireContext().getString(R.string.backup_last_searches_file_name) + ".txt")
                        try {
                            FileUtils.readFileData(
                                requireContext(),
                                directoryPath1,
                                directoryPath2
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                true
            }
            val autoSearch = findPreference("remember_last_search") as TwoStatePreference
            autoSearch.isChecked = Utils.getAutoSearch(requireContext())
            autoSearch.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    Utils.setAutoSearch(
                        requireContext(),
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