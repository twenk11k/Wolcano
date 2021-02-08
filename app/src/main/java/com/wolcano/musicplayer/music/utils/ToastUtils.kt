package com.wolcano.musicplayer.music.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

object ToastUtils {

    private var toastMsg: Toast? = null

    @SuppressLint("ShowToast")
    fun show(context: Context?, str: String?) {
        if (toastMsg == null) {
            toastMsg = Toast.makeText(context, str, Toast.LENGTH_SHORT)
        } else {
            toastMsg!!.setText(str)
        }
        toastMsg?.show()
    }

    fun show(context: Context, id: Int) {
        show(context, context.getString(id))
    }

}