package com.wolcano.musicplayer.music.ui.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.*
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.view.menu.*
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.ui.helper.TintHelper.createTintedDrawable
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper.InternalToolbarContentTintUtil.applyOverflowMenuTint
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.Utils
import java.lang.reflect.Field

object ToolbarContentTintHelper {

    fun handleOnCreateOptionsMenu(
        context: Context,
        toolbar: Toolbar?,
        menu: Menu?,
        toolbarColor: Int
    ) {
        setToolbarContentColorBasedOnToolbarColor(context, toolbar, menu, toolbarColor)
    }

    fun setToolbarContentColorBasedOnToolbarColor(
        context: Context,
        toolbar: Toolbar?,
        @Nullable menu: Menu?,
        toolbarColor: Int
    ) {
        setToolbarContentColorBasedOnToolbarColor(
            context,
            toolbar,
            menu,
            toolbarColor,
            Utils.getAccentColor(context)
        )
    }

    fun setToolbarContentColorBasedOnToolbarColor(
        context: Context,
        toolbar: Toolbar?,
        @Nullable menu: Menu?,
        toolbarColor: Int,
        @ColorInt menuWidgetColor: Int
    ) {
        setToolbarContentColor(
            context,
            toolbar,
            menu,
            toolbarContentColor(context, toolbarColor),
            toolbarTitleColor(context, toolbarColor),
            toolbarSubtitleColor(context, toolbarColor),
            menuWidgetColor
        )
    }

    @SuppressLint("RestrictedApi")
    fun setToolbarContentColor(
        context: Context,
        toolbar: Toolbar?,
        menu: Menu?,
        @ColorInt toolbarContentColor: Int,
        @ColorInt titleTextColor: Int,
        @ColorInt subtitleTextColor: Int,
        @ColorInt menuWidgetColor: Int
    ) {
        var menu = menu
        if (toolbar == null) return
        if (menu == null) {
            menu = toolbar.menu
        }
        toolbar.setTitleTextColor(titleTextColor)
        toolbar.setSubtitleTextColor(subtitleTextColor)
        if (toolbar.navigationIcon != null) {
            // Tint the toolbar navigation icon (e.g. back, drawer, etc.)
            toolbar.navigationIcon = createTintedDrawable(
                toolbar.navigationIcon,
                toolbarContentColor
            )
        }
        InternalToolbarContentTintUtil.tintMenu(toolbar, menu, toolbarContentColor)
        applyOverflowMenuTint(context, toolbar, menuWidgetColor)
        if (context is Activity) {
            InternalToolbarContentTintUtil.setOverflowButtonColor(context, toolbarContentColor)
        }
        try {
            // Tint immediate overflow menu items
            val menuField: Field = Toolbar::class.java.getDeclaredField("mMenuBuilderCallback")
            menuField.isAccessible = true
            val presenterField: Field =
                Toolbar::class.java.getDeclaredField("mActionMenuPresenterCallback")
            presenterField.isAccessible = true
            val menuViewField: Field = Toolbar::class.java.getDeclaredField("mMenuView")
            menuViewField.isAccessible = true
            val currentPresenterCb = presenterField.get(toolbar) as MenuPresenter.Callback
            if (currentPresenterCb !is ATHMenuPresenterCallback) {
                val newPresenterCb =
                    ATHMenuPresenterCallback(context, menuWidgetColor, currentPresenterCb, toolbar)
                val currentMenuCb = menuField.get(toolbar) as MenuBuilder.Callback?
                toolbar.setMenuCallbacks(newPresenterCb, currentMenuCb)
                val menuView: ActionMenuView = menuViewField.get(toolbar) as ActionMenuView
                menuView.setMenuCallbacks(newPresenterCb, currentMenuCb)
            }

            // OnMenuItemClickListener to tint submenu items
            val menuItemClickListener: Field =
                Toolbar::class.java.getDeclaredField("mOnMenuItemClickListener")
            menuItemClickListener.isAccessible = true
            val currentClickListener =
                menuItemClickListener.get(toolbar) as Toolbar.OnMenuItemClickListener
            if (currentClickListener !is ATHOnMenuItemClickListener) {
                val newClickListener = ATHOnMenuItemClickListener(
                    context,
                    menuWidgetColor,
                    currentClickListener,
                    toolbar
                )
                toolbar.setOnMenuItemClickListener(newClickListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @CheckResult
    @ColorInt
    fun toolbarContentColor(context: Context, @ColorInt toolbarColor: Int): Int {
        return if (ColorUtils.isColorLight(toolbarColor)) {
            toolbarSubtitleColor(context, toolbarColor)
        } else toolbarTitleColor(context, toolbarColor)
    }

    @CheckResult
    @ColorInt
    fun toolbarSubtitleColor(context: Context, @ColorInt toolbarColor: Int): Int {
        return MaterialValueHelper.getSecondaryTextColor(
            context,
            ColorUtils.isColorLight(toolbarColor)
        )
    }

    @CheckResult
    @ColorInt
    fun toolbarTitleColor(context: Context, @ColorInt toolbarColor: Int): Int {
        return MaterialValueHelper.getPrimaryTextColor(
            context,
            ColorUtils.isColorLight(toolbarColor)
        )
    }

    object InternalToolbarContentTintUtil {
        fun tintMenu(toolbar: Toolbar, menu: Menu?, @ColorInt color: Int) {
            try {
                val field = Toolbar::class.java.getDeclaredField("mCollapseIcon")
                field.isAccessible = true
                val collapseIcon = field[toolbar] as Drawable
                field[toolbar] = createTintedDrawable(collapseIcon, color)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            if (menu != null && menu.size() > 0) {
                for (i in 0 until menu.size()) {
                    val item: MenuItem = menu.getItem(i)
                    if (item.icon != null) {
                        item.icon = createTintedDrawable(item.icon, color)
                    }
                    // Search view theming
                    if (item.actionView != null && (item.actionView is SearchView || item.actionView is SearchView)) {
                        SearchViewTintUtil.setSearchViewContentColor(item.actionView, color)
                    }
                }
            }
        }

        fun applyOverflowMenuTint(context: Context, toolbar: Toolbar?, @ColorInt color: Int) {
            if (toolbar == null) return
            toolbar.post(Runnable {
                try {
                    val f1 = Toolbar::class.java.getDeclaredField("mMenuView")
                    f1.isAccessible = true
                    val actionMenuView = f1[toolbar] as ActionMenuView
                    val f2 = ActionMenuView::class.java.getDeclaredField("mPresenter")
                    f2.isAccessible = true

                    // Actually ActionMenuPresenter
                    val presenter = f2[actionMenuView] as BaseMenuPresenter
                    val f3 = presenter.javaClass.getDeclaredField("mOverflowPopup")
                    f3.isAccessible = true
                    val overflowMenuPopupHelper = f3[presenter] as MenuPopupHelper
                    setTintForMenuPopupHelper(context, overflowMenuPopupHelper, color)
                    val f4 = presenter.javaClass.getDeclaredField("mActionButtonPopup")
                    f4.isAccessible = true
                    val subMenuPopupHelper = f4[presenter] as MenuPopupHelper
                    setTintForMenuPopupHelper(context, subMenuPopupHelper, color)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            })
        }

        @SuppressLint("RestrictedApi")
        fun setTintForMenuPopupHelper(
            context: Context,
            menuPopupHelper: MenuPopupHelper?,
            @ColorInt color: Int
        ) {
            try {
                if (menuPopupHelper != null) {
                    val listView: ListView = (menuPopupHelper.popup as ShowableListMenu).listView
                    listView.viewTreeObserver
                        .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                try {
                                    val checkboxField =
                                        ListMenuItemView::class.java.getDeclaredField("mCheckBox")
                                    checkboxField.isAccessible = true
                                    val radioButtonField =
                                        ListMenuItemView::class.java.getDeclaredField("mRadioButton")
                                    radioButtonField.isAccessible = true
                                    val isDark: Boolean = !ColorUtils.isColorLight(
                                        ColorUtils.resolveColor(
                                            context,
                                            android.R.attr.windowBackground
                                        )
                                    )
                                    for (i in 0 until listView.childCount) {
                                        val v: View = listView.getChildAt(i) as? ListMenuItemView
                                            ?: continue
                                        val iv = v
                                        val check = checkboxField[iv] as CheckBox
                                        TintHelper.setTint(check, color, isDark)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) check.background =
                                            null
                                        val radioButton = radioButtonField[iv] as RadioButton
                                        TintHelper.setTint(radioButton, color, isDark)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) radioButton.background =
                                            null
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                                listView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        })
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun setOverflowButtonColor(@NonNull activity: Activity, @ColorInt color: Int) {
            val overflowDescription =
                activity.getString(R.string.abc_action_menu_overflow_description)
            val decorView = activity.window.decorView as ViewGroup
            val viewTreeObserver = decorView.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val outViews: ArrayList<View> = ArrayList()
                    decorView.findViewsWithText(
                        outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
                    )
                    if (outViews.isEmpty()) return
                    val overflow = outViews[0] as AppCompatImageView
                    overflow.setImageDrawable(createTintedDrawable(overflow.drawable, color))
                    decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        object SearchViewTintUtil {
            @Throws(java.lang.Exception::class)
            private fun tintImageView(target: Any, field: Field, @ColorInt color: Int) {
                field.isAccessible = true
                val imageView: ImageView = field[target] as ImageView
                if (imageView.drawable != null) imageView.setImageDrawable(
                    createTintedDrawable(
                        imageView.drawable,
                        color
                    )
                )
            }

            fun setSearchViewContentColor(searchView: View?, @ColorInt color: Int) {
                if (searchView == null) return
                val cls: Class<*> = searchView.javaClass
                try {
                    val mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView")
                    mSearchSrcTextViewField.isAccessible = true
                    val mSearchSrcTextView = mSearchSrcTextViewField[searchView] as EditText
                    mSearchSrcTextView.setTextColor(color)
                    mSearchSrcTextView.setHintTextColor(ColorUtils.adjustAlpha(color, 0.5f))
                    TintHelper.setCursorTint(mSearchSrcTextView, color)
                    var field = cls.getDeclaredField("mSearchButton")
                    tintImageView(searchView, field, color)
                    field = cls.getDeclaredField("mGoButton")
                    tintImageView(searchView, field, color)
                    field = cls.getDeclaredField("mCloseButton")
                    tintImageView(searchView, field, color)
                    field = cls.getDeclaredField("mVoiceButton")
                    tintImageView(searchView, field, color)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private class ATHOnMenuItemClickListener(
        private val mContext: Context,
        @param:ColorInt private val mColor: Int,
        private val mParentListener: Toolbar.OnMenuItemClickListener?,
        private val mToolbar: Toolbar
    ) :
        Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            applyOverflowMenuTint(mContext, mToolbar, mColor)
            return mParentListener != null && mParentListener.onMenuItemClick(item)
        }
    }

    private class ATHMenuPresenterCallback(
        private val mContext: Context,
        @param:ColorInt private val mColor: Int,
        private val mParentCb: MenuPresenter.Callback?,
        private val mToolbar: Toolbar
    ) :
        MenuPresenter.Callback {
        override fun onCloseMenu(menu: MenuBuilder, allMenusAreClosing: Boolean) {
            mParentCb?.onCloseMenu(menu, allMenusAreClosing)
        }

        override fun onOpenSubMenu(subMenu: MenuBuilder): Boolean {
            applyOverflowMenuTint(mContext, mToolbar, mColor)
            return mParentCb != null && mParentCb.onOpenSubMenu(subMenu)
        }
    }

}