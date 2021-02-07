package com.wolcano.musicplayer.music.utils

import android.graphics.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object ImageUtils {

    private const val RADIAN = 50

    fun chgImage(bitmap: Bitmap?, destWidth: Int, destHeight: Int): Bitmap? {
        if (bitmap == null) {
            return null
        }
        return if (bitmap.width == destWidth && bitmap.height == destHeight) {
            bitmap
        } else Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, true)
    }

    /**
     * Method for blur image
     *
     * @param bitmap
     * @return
     */
    fun blur(bitmap: Bitmap?): Bitmap? {
        return if (bitmap == null) {
            null
        } else try {
            blur(bitmap, RADIAN)
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }

    /**
     * Method for blur image with given radian value
     *
     * @param bitmap1
     * @param RADIAN
     * @return
     */
    private fun blur(bitmap1: Bitmap, RADIAN: Int): Bitmap? {
        val bitmap = bitmap1.copy(bitmap1.config, true)
        if (RADIAN < 1) {
            return null
        }
        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = RADIAN + RADIAN + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rSum: Int
        var gSum: Int
        var bSum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vMin = IntArray(max(w, h))
        var divSum = div + 1 shr 1
        divSum *= divSum
        val dv = IntArray(256 * divSum)
        i = 0
        while (i < 256 * divSum) {
            dv[i] = i / divSum
            i++
        }
        yi = 0
        yw = yi
        val stack = Array(div) {
            IntArray(
                3
            )
        }
        var stackPointer: Int
        var stackStart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = RADIAN + 1
        var rOutSum: Int
        var gOutSum: Int
        var bOutSum: Int
        var rInSum: Int
        var gInSum: Int
        var bInSum: Int
        y = 0
        while (y < h) {
            bSum = 0
            gSum = bSum
            rSum = gSum
            bOutSum = rSum
            gOutSum = bOutSum
            rOutSum = gOutSum
            bInSum = rOutSum
            gInSum = bInSum
            rInSum = gInSum
            i = -RADIAN
            while (i <= RADIAN) {
                p = pix[yi + min(wm, max(i, 0))]
                sir = stack[i + RADIAN]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - abs(i)
                rSum += sir[0] * rbs
                gSum += sir[1] * rbs
                bSum += sir[2] * rbs
                if (i > 0) {
                    rInSum += sir[0]
                    gInSum += sir[1]
                    bInSum += sir[2]
                } else {
                    rOutSum += sir[0]
                    gOutSum += sir[1]
                    bOutSum += sir[2]
                }
                i++
            }
            stackPointer = RADIAN
            x = 0
            while (x < w) {
                r[yi] = dv[rSum]
                g[yi] = dv[gSum]
                b[yi] = dv[bSum]
                rSum -= rOutSum
                gSum -= gOutSum
                bSum -= bOutSum
                stackStart = stackPointer - RADIAN + div
                sir = stack[stackStart % div]
                rOutSum -= sir[0]
                gOutSum -= sir[1]
                bOutSum -= sir[2]
                if (y == 0) {
                    vMin[x] = min(x + RADIAN + 1, wm)
                }
                p = pix[yw + vMin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rInSum += sir[0]
                gInSum += sir[1]
                bInSum += sir[2]
                rSum += rInSum
                gSum += gInSum
                bSum += bInSum
                stackPointer = (stackPointer + 1) % div
                sir = stack[stackPointer % div]
                rOutSum += sir[0]
                gOutSum += sir[1]
                bOutSum += sir[2]
                rInSum -= sir[0]
                gInSum -= sir[1]
                bInSum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bSum = 0
            gSum = bSum
            rSum = gSum
            bOutSum = rSum
            gOutSum = bOutSum
            rOutSum = gOutSum
            bInSum = rOutSum
            gInSum = bInSum
            rInSum = gInSum
            yp = -RADIAN * w
            i = -RADIAN
            while (i <= RADIAN) {
                yi = max(0, yp) + x
                sir = stack[i + RADIAN]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - abs(i)
                rSum += r[yi] * rbs
                gSum += g[yi] * rbs
                bSum += b[yi] * rbs
                if (i > 0) {
                    rInSum += sir[0]
                    gInSum += sir[1]
                    bInSum += sir[2]
                } else {
                    rOutSum += sir[0]
                    gOutSum += sir[1]
                    bOutSum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackPointer = RADIAN
            y = 0
            while (y < h) {

                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rSum] shl 16) or (dv[gSum] shl 8) or dv[bSum]
                rSum -= rOutSum
                gSum -= gOutSum
                bSum -= bOutSum
                stackStart = stackPointer - RADIAN + div
                sir = stack[stackStart % div]
                rOutSum -= sir[0]
                gOutSum -= sir[1]
                bOutSum -= sir[2]
                if (x == 0) {
                    vMin[y] = min(y + r1, hm) * w
                }
                p = x + vMin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rInSum += sir[0]
                gInSum += sir[1]
                bInSum += sir[2]
                rSum += rInSum
                gSum += gInSum
                bSum += bInSum
                stackPointer = (stackPointer + 1) % div
                sir = stack[stackPointer]
                rOutSum += sir[0]
                gOutSum += sir[1]
                bOutSum += sir[2]
                rInSum -= sir[0]
                gInSum -= sir[1]
                bInSum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    /**
     * Method for tint Bitmap with given color
     * 2016
     *
     * @param bitmap
     * @param color
     * @return
     */
    fun tintBitmap(bitmap: Bitmap, color: Int): Bitmap {
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
        val bitmapResult = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmapResult)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bitmapResult
    }

    /**
     * Method for append oval bitmap
     *
     * @param bitmap
     * @return
     */
    fun appendOvalImg(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val size = min(bitmap.width, bitmap.height)
        val paint = Paint()
        paint.isAntiAlias = true
        val bitmap2 = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap2)
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat(),
            (size / 2).toFloat(),
            paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bitmap2
    }

}