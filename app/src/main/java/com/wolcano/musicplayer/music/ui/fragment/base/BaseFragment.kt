package com.wolcano.musicplayer.music.ui.fragment.base

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.hwangjr.rxbus.RxBus
import com.wolcano.musicplayer.music.content.Binder
import com.wolcano.musicplayer.music.ui.activity.main.MainActivity
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.widgets.StatusBarView

open class BaseFragment : Fragment() {

    protected var handler: Handler? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        Binder.bindIt(this, view)
        RxBus.get().register(this)
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

    open fun setStatusBarColor(color: Int, statusBarView: StatusBarView?) {
        if (statusBarView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarView.setBackgroundColor(ColorUtils.darkenColor(color))
                (activity as MainActivity?)!!.setLightStatusBarAuto(color)
            } else {
                statusBarView.setBackgroundColor(color)
            }
        }
    }

}