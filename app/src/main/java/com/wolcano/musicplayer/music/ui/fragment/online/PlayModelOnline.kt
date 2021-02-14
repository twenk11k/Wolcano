package com.wolcano.musicplayer.music.ui.fragment.online

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.listener.TaskInterface
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.utils.NetworkUtils
import com.wolcano.musicplayer.music.utils.Utils

abstract class PlayModelOnline(private val activity: Activity) : TaskInterface<Song> {

    protected var song: Song? = null
    protected var songList: MutableList<Song>? = null

    override fun onTask() {
        checkMobileConnection()
    }

    private fun getPlayModelImpl() {
        onPrepare()
        setPlayModel()
    }

    protected abstract fun setPlayModel()

    private fun checkMobileConnection() {
        val isMobileNet: Boolean = Utils.getMobileInternet(activity.applicationContext)
        if (NetworkUtils.isMobileActive(activity) && !isMobileNet) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(R.string.warning)
            builder.setMessage(R.string.control_inter_message)
            builder.setNegativeButton(R.string.no, null)
            builder.setPositiveButton(R.string.yes) { _, _ ->
                Utils.getMobileInternet(activity.applicationContext)
                getPlayModelImpl()
            }
            val mobileDialog: Dialog = builder.create()
            mobileDialog.setCanceledOnTouchOutside(false)
            mobileDialog.show()
        } else {
            getPlayModelImpl()
        }
    }

}