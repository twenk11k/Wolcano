package com.wolcano.musicplayer.music.ui.helper

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.ColorUtils.adjustAlpha
import com.wolcano.musicplayer.music.utils.ColorUtils.shiftColor
import com.wolcano.musicplayer.music.utils.ColorUtils.stripAlpha
import java.lang.reflect.Field

object TintHelper {
    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary because Drawables with the same resource have shared states otherwise.
    @CheckResult
    @Nullable
    fun createTintedDrawable(@Nullable drawable: Drawable?, @ColorInt color: Int): Drawable? {
        var drawable: Drawable? = drawable ?: return null
        drawable = DrawableCompat.wrap(drawable!!.mutate())
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
        DrawableCompat.setTint(drawable, color)
        return drawable
    }

    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary because Drawables with the same resource have shared states otherwise.
    @CheckResult
    @Nullable
    fun createTintedDrawable(
        @Nullable drawable: Drawable?,
        @NonNull sl: ColorStateList?
    ): Drawable? {
        var drawable: Drawable? = drawable ?: return null
        drawable = DrawableCompat.wrap(drawable!!.mutate())
        DrawableCompat.setTintList(drawable, sl)
        return drawable
    }

    fun setTint(box: CheckBox, @ColorInt color: Int, useDarker: Boolean) {
        val sl = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
                intArrayOf(
                    android.R.attr.state_enabled, android.R.attr.state_checked
                )
            ), intArrayOf(
                ContextCompat.getColor(
                    box.context,
                    if (useDarker) R.color.ate_control_disabled_dark else R.color.ate_control_disabled_light
                ),
                ContextCompat.getColor(
                    box.context,
                    if (useDarker) R.color.ate_control_normal_dark else R.color.ate_control_normal_light
                ),
                color
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            box.buttonTintList = sl
        } else {
            val drawable = createTintedDrawable(
                ContextCompat.getDrawable(
                    box.context,
                    R.drawable.abc_btn_check_material
                ), sl
            )
            box.buttonDrawable = drawable
        }
    }

    fun setTint(radioButton: RadioButton, @ColorInt color: Int, useDarker: Boolean) {
        val sl = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
            ), intArrayOf( // Rdio button includes own alpha for disabled state
                ColorUtils.stripAlpha(
                    ContextCompat.getColor(
                        radioButton.context,
                        if (useDarker) R.color.ate_control_disabled_dark else R.color.ate_control_disabled_light
                    )
                ),
                ContextCompat.getColor(
                    radioButton.context,
                    if (useDarker) R.color.ate_control_normal_dark else R.color.ate_control_normal_light
                ),
                color
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButton.buttonTintList = sl
        } else {
            val d = createTintedDrawable(
                ContextCompat.getDrawable(
                    radioButton.context,
                    R.drawable.abc_btn_radio_material
                ), sl
            )
            radioButton.buttonDrawable = d
        }
    }

    fun setTint(switchView: SwitchCompat, @ColorInt color: Int, useDarker: Boolean) {
        if (switchView.trackDrawable != null) {
            switchView.trackDrawable = modifySwitchDrawable(
                switchView.context,
                switchView.trackDrawable, color, false, true, useDarker
            )
        }
        if (switchView.thumbDrawable != null) {
            switchView.thumbDrawable = modifySwitchDrawable(
                switchView.context,
                switchView.thumbDrawable, color, true, true, useDarker
            )
        }
    }

    private fun modifySwitchDrawable(
        context: Context,
        from: Drawable,
        @ColorInt tint: Int,
        thumb: Boolean,
        compatSwitch: Boolean,
        useDarker: Boolean
    ): Drawable? {
        var tint = tint
        if (useDarker) {
            tint = shiftColor(tint, 1.1f)
        }
        tint = adjustAlpha(tint, if (compatSwitch && !thumb) 0.5f else 1.0f)
        val disabled: Int
        var normal: Int
        if (thumb) {
            disabled = ContextCompat.getColor(
                context,
                if (useDarker) R.color.ate_switch_thumb_disabled_dark else R.color.ate_switch_thumb_disabled_light
            )
            normal = ContextCompat.getColor(
                context,
                if (useDarker) R.color.ate_switch_thumb_normal_dark else R.color.ate_switch_thumb_normal_light
            )
        } else {
            disabled = ContextCompat.getColor(
                context,
                if (useDarker) R.color.ate_switch_track_disabled_dark else R.color.ate_switch_track_disabled_light
            )
            normal = ContextCompat.getColor(
                context,
                if (useDarker) R.color.ate_switch_track_normal_dark else R.color.ate_switch_track_normal_light
            )
        }

        // Stock switch includes its own alpha
        if (!compatSwitch) {
            normal = stripAlpha(normal)
        }
        val sl = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(
                    android.R.attr.state_enabled,
                    -android.R.attr.state_activated,
                    -android.R.attr.state_checked
                ),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
            ), intArrayOf(
                disabled,
                normal,
                tint,
                tint
            )
        )
        return createTintedDrawable(from, sl)
    }

    fun setCursorTint(editText: EditText, @ColorInt color: Int) {
        try {
            val fCursorDrawableRes: Field =
                TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.setAccessible(true)
            val mCursorDrawableRes: Int = fCursorDrawableRes.getInt(editText)
            val fEditor: Field = TextView::class.java.getDeclaredField("mEditor")
            fEditor.setAccessible(true)
            val editor: Any = fEditor.get(editText)
            val clazz: Class<*> = editor.javaClass
            val fCursorDrawable: Field = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.setAccessible(true)
            val drawables = arrayOfNulls<Drawable>(2)
            drawables[0] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
            drawables[0] = createTintedDrawable(drawables[0], color)
            drawables[1] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
            drawables[1] = createTintedDrawable(drawables[1], color)
            fCursorDrawable.set(editor, drawables)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}