package com.wolcano.musicplayer.music.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

class ScaledImageView(context: Context?) : View(context) {

    var imageBitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (imageBitmap != null) {
            canvas.drawBitmap(imageBitmap!!, 0f, 0f, null)
        }
    }

}