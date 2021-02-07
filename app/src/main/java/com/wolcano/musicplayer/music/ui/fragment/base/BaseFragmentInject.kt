package com.wolcano.musicplayer.music.ui.fragment.base

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.hwangjr.rxbus.RxBus
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.content.Binder.bindIt
import com.wolcano.musicplayer.music.di.component.ApplicationComponent
import com.wolcano.musicplayer.music.ui.activity.MainActivity
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.widgets.StatusBarView

abstract class BaseFragmentInject : Fragment() {

    protected var handler: Handler? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        bindIt(this, view)
        RxBus.get().register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupComponent((activity!!.application as App).getApplicationComponent())
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
        RxBus.get().unregister(this)
        super.onDestroy()
    }

    open fun setStatusbarColor(color: Int, statusBarView: StatusBarView?) {
        if (statusBarView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarView.setBackgroundColor(ColorUtils.darkenColor(color))
                (activity as MainActivity?)?.setLightStatusbarAuto(color)
            } else {
                statusBarView.setBackgroundColor(color)
            }
        }
    }

    abstract fun setupComponent(applicationComponent: ApplicationComponent?)

}