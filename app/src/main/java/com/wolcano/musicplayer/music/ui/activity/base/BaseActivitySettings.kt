package com.wolcano.musicplayer.music.ui.activity.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.binding.DataBindingActivity
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.ThemeUtils
import com.wolcano.musicplayer.music.utils.Utils
import com.wolcano.musicplayer.music.widgets.StatusBarView

open class BaseActivitySettings : DataBindingActivity() {

    val PERMISSION_REQUEST = 100

    private var hadPermissions = false
    private var permissions: Array<String?>? = null
    private var permissionDeniedMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        permissions = getPermissionsToRequest()
        hadPermissions = hasPermissions()
        setPermissionDeniedMessage(null)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (!hasPermissions()) {
            requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        val hasPermissions: Boolean = hasPermissions()
        if (hasPermissions != hadPermissions) {
            hadPermissions = hasPermissions
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_MENU && event.action == KeyEvent.ACTION_UP) {
            showOverflowMenu()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private fun showOverflowMenu() {}

    private fun getPermissionsToRequest(): Array<String?>? {
        return null
    }

    private fun getSnackBarContainer(): View? {
        return window.decorView
    }

    private fun setPermissionDeniedMessage(message: String?) {
        permissionDeniedMessage = message
    }

    private fun getPermissionDeniedMessage(): String? {
        return if (permissionDeniedMessage == null) getString(R.string.permissions_denied) else permissionDeniedMessage
    }

    protected fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            requestPermissions(permissions, PERMISSION_REQUEST)
        }
    }

    private fun hasPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (permission in permissions!!) {
                if (checkSelfPermission(permission!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@BaseActivitySettings,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        //User has deny from permission dialog
                        Snackbar.make(
                            getSnackBarContainer()!!, getPermissionDeniedMessage()!!,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(R.string.action_grant) { requestPermissions() }
                            .setActionTextColor(Utils.getAccentColor(this))
                            .show()
                    } else {
                        // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                        Snackbar.make(
                            getSnackBarContainer()!!, getPermissionDeniedMessage()!!,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(R.string.settings) { view ->
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                    "package",
                                    this@BaseActivitySettings.packageName,
                                    null
                                )
                                intent.data = uri
                                startActivity(intent)
                            }
                            .setActionTextColor(Utils.getAccentColor(this))
                            .show()
                    }
                    return
                }
            }
            hadPermissions = true
        }
    }

    fun setStatusBarColor(color: Int, statusBarView: StatusBarView?) {
        if (statusBarView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarView.setBackgroundColor(ColorUtils.darkenColor(color))
                setLightStatusBarAuto(color)
            } else {
                statusBarView.setBackgroundColor(color)
            }
        }
    }

    private fun setLightStatusBar(enabled: Boolean) {
        ThemeUtils.setLightStatusBar(this, enabled)
    }

    private fun setLightStatusBarAuto(bgColor: Int) {
        setLightStatusBar(ColorUtils.isColorLight(bgColor))
    }

    protected fun setDrawUnderStatusBar() {
        Utils.setAllowDrawUnderStatusBar(window)
    }

    fun setTaskDescriptionColorAuto() {
        setTaskDescriptionColor(Utils.getPrimaryColor(this))
    }

    private fun setTaskDescriptionColor(@ColorInt color: Int) {
        ThemeUtils.setTaskDescriptionColor(this, color)
    }

}