package com.wolcano.musicplayer.music.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {

    fun isMobileActive(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_MOBILE
    }

}