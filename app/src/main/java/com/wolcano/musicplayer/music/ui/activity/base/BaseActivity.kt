package com.wolcano.musicplayer.music.ui.activity.base

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.os.*
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.hwangjr.rxbus.RxBus
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.content.Binder
import com.wolcano.musicplayer.music.provider.MusicService
import com.wolcano.musicplayer.music.provider.MusicService.ServiceInit
import com.wolcano.musicplayer.music.utils.ATH
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.Utils
import com.wolcano.musicplayer.music.widgets.StatusBarView

open class BaseActivity : DataBindingActivity() {

    protected var musicService: MusicService? = null
    private var serviceConnection: ServiceConnection? = null
    protected var baseHandler: Handler? = null
    private var receiverRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // status bar
        setStatusBar()
        // volume control
        volumeControlStream = AudioManager.STREAM_MUSIC
        // handler
        baseHandler = Handler(Looper.getMainLooper())
        // service connection
        connectService()
        RxBus.get().register(this)
    }


    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setView()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setView()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        setView()
    }

    private fun connectService() {
        val intent = Intent()
        intent.setClass(this, MusicService::class.java)
        serviceConnection = RemoteServiceConn()
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }


    private fun setView() {
        Binder.bindIt(this)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private inner class RemoteServiceConn : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            musicService = (service as ServiceInit).musicService
            if (!Utils.getServiceDestroy(applicationContext)) {
                onServiceConnection()
                handleListener()
            } else {
                Utils.setServiceDestroy(applicationContext, false)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Utils.setServiceDestroy(applicationContext, true)
        }
    }

    protected open fun onServiceConnection() {}

    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection)
        }
        if (receiverRegistered) {
            receiverRegistered = false
        }
        if (Build.VERSION.SDK_INT > 19) RxBus.get().unregister(this)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (Utils.getServiceDestroy(this)) {
            val intent = Intent()
            intent.setClass(this, MusicService::class.java)
            if (serviceConnection != null) {
                bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            } else {
                serviceConnection = RemoteServiceConn()
                bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            }
        }
    }

    protected open fun handleListener() {}

    open fun setLightStatusbar(enabled: Boolean) {
        ATH.setLightStatusbar(this, enabled)
    }

    open fun setLightStatusbarAuto(bgColor: Int) {
        setLightStatusbar(ColorUtils.isColorLight(bgColor))
    }

    open fun setStatusbarColor(color: Int, statusBarView: StatusBarView?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (statusBarView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    statusBarView.setBackgroundColor(ColorUtils.darkenColor(color))
                    setLightStatusbarAuto(color)
                } else {
                    statusBarView.setBackgroundColor(color)
                }
            }
        }
    }

}