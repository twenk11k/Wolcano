package com.wolcano.musicplayer.music.widgets

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.utils.Utils.isColorLight
import java.lang.reflect.InvocationTargetException

class RotateFabBehavior : CoordinatorLayout.Behavior<FloatingActionButton> {

    private val AUTO_HIDE_DEFAULT = true

    private var mTmpRect: Rect? = null
    private val mInternalAutoHideListener: OnVisibilityChangedListener? = null
    private var mAutoHideEnabled = false

    constructor() : super() {
        mAutoHideEnabled = AUTO_HIDE_DEFAULT
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.FloatingActionButton_Behavior_Layout
        )
        mAutoHideEnabled = a.getBoolean(
            R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide,
            AUTO_HIDE_DEFAULT
        )
        a.recycle()
    }

    companion object {

        private var isAnimate = false

        fun show(floatingActionButton: FloatingActionButton, color: Int, fromUser: Boolean) {
            if (fromUser) {
                floatingActionButton.backgroundTintList = ColorStateList.valueOf(color)
                if (!isColorLight(color)) {
                    floatingActionButton.setColorFilter(Color.WHITE)
                } else {
                    floatingActionButton.setColorFilter(Color.BLACK)
                }
            }
            floatingActionButton.postDelayed({
                val fabAnimation = AnimatorInflater.loadAnimator(
                    floatingActionButton.context,
                    R.animator.float_button_in
                ) as AnimatorSet
                fabAnimation.setTarget(floatingActionButton)
                fabAnimation.interpolator = FastOutSlowInInterpolator()
                fabAnimation.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        isAnimate = true
                        floatingActionButton.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isAnimate = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        hide(floatingActionButton)
                    }

                    override fun onAnimationRepeat(animation: Animator) {}
                })
                fabAnimation.start()
            }, 150L)
        }

        private fun hide(floatingActionButton: FloatingActionButton) {
            val fabAnimation = AnimatorInflater.loadAnimator(
                floatingActionButton.context,
                R.animator.float_button_out
            ) as AnimatorSet
            fabAnimation.setTarget(floatingActionButton)
            fabAnimation.interpolator = FastOutSlowInInterpolator()
            fabAnimation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isAnimate = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    floatingActionButton.visibility = View.GONE
                    isAnimate = false
                }

                override fun onAnimationCancel(animation: Animator) {
                    show(floatingActionButton, 0, false)
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
            fabAnimation.start()
        }

    }

    override fun onAttachedToLayoutParams(lp: CoordinatorLayout.LayoutParams) {
        if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            // If the developer hasn't set dodgeInsetEdges, lets set it to BOTTOM so that
            // we dodge any Snackbars
            lp.dodgeInsetEdges = Gravity.BOTTOM
        }
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout, child: FloatingActionButton,
        dependency: View
    ): Boolean {
        if (dependency is AppBarLayout) {
            // If we're depending on an AppBarLayout we will show/hide it automatically
            // if the FAB is anchored to the AppBarLayout
            updateFabVisibilityForAppBarLayout(parent, dependency, child)
        }
        return false
    }

    private fun shouldUpdateVisibility(dependency: View, child: FloatingActionButton): Boolean {
        val lp = child.layoutParams as CoordinatorLayout.LayoutParams
        return mAutoHideEnabled
    }


    private fun updateFabVisibilityForAppBarLayout(
        parent: CoordinatorLayout,
        appBarLayout: AppBarLayout, child: FloatingActionButton
    ): Boolean {
        if (!shouldUpdateVisibility(appBarLayout, child)) {
            return false
        }
        if (mTmpRect == null) {
            mTmpRect = Rect()
        }

        // First, let's get the visible rect of the dependency
        val rect: Rect = mTmpRect!!
        getDescendantRect(parent, appBarLayout, rect)
        var height = 0
        try {
            val method = AppBarLayout::class.java.getDeclaredMethod(
                "getMinimumHeightForVisibleOverlappingContent",
                *arrayOfNulls<Class<*>?>(0)
            )
            method.isAccessible = true
            height = method.invoke(appBarLayout, *arrayOfNulls(0)) as Int
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        if (rect.bottom <= height) {
            // If the anchor's bottom is below the seam, we'll animate our FAB out
            if (!isAnimate && child.visibility == View.VISIBLE) {
                hide(child)
            }
        } else {

            // Else, we'll animate our FAB back in
            if (!isAnimate && child.visibility == View.GONE) {
                show(child, 0, false)
            }
        }
        return true
    }


    fun onLayoutChild(
        parent: CoordinatorLayout, child: FloatingActionButton?,
        layoutDirection: Int
    ): Boolean {
        // First, let's make sure that the visibility of the FAB is consistent
        val dependencies = parent.getDependencies(child!!)
        var i = 0
        val count = dependencies.size
        while (i < count) {
            val dependency = dependencies[i]
            if (dependency is AppBarLayout) {
                if (updateFabVisibilityForAppBarLayout(
                        parent, dependency, child
                    )
                ) {
                    break
                }
            }
            i++
        }
        // Now let the CoordinatorLayout lay out the FAB
        parent.onLayoutChild(child, layoutDirection)
        // Now offset it if needed
        offsetIfNeeded(parent, child)
        return true
    }

    override fun getInsetDodgeRect(
        parent: CoordinatorLayout,
        child: FloatingActionButton, rect: Rect
    ): Boolean {
        // Since we offset so that any internal shadow padding isn't shown, we need to make
        // sure that the shadow isn't used for any dodge inset calculations
        var shadowPadding = Rect(0, 0, 0, 0)
        try {
            val mShadowPadding = FloatingActionButton::class.java.getDeclaredField("mShadowPadding")
            mShadowPadding.isAccessible = true
            shadowPadding = mShadowPadding[child] as Rect
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        rect[child.left + shadowPadding.left, child.top + shadowPadding.top, child.right - shadowPadding.right] =
            child.bottom - shadowPadding.bottom
        return true
    }


    /**
     * Pre-Lollipop we use padding so that the shadow has enough space to be drawn. This method
     * offsets our layout position so that we're positioned correctly if we're on one of
     * our parent's edges.
     */
    private fun offsetIfNeeded(parent: CoordinatorLayout, fab: FloatingActionButton) {
        var padding = Rect(0, 0, 0, 0)
        try {
            val mShadowPadding = FloatingActionButton::class.java.getDeclaredField("mShadowPadding")
            mShadowPadding.isAccessible = true
            padding = mShadowPadding[fab] as Rect
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        if (padding != null && padding.centerX() > 0 && padding.centerY() > 0) {
            val lp = fab.layoutParams as CoordinatorLayout.LayoutParams
            var offsetTB = 0
            var offsetLR = 0
            if (fab.right >= parent.width - lp.rightMargin) {
                // If we're on the right edge, shift it the right
                offsetLR = padding.right
            } else if (fab.left <= lp.leftMargin) {
                // If we're on the left edge, shift it the left
                offsetLR = -padding.left
            }
            if (fab.bottom >= parent.height - lp.bottomMargin) {
                // If we're on the bottom edge, shift it down
                offsetTB = padding.bottom
            } else if (fab.top <= lp.topMargin) {
                // If we're on the top edge, shift it up
                offsetTB = -padding.top
            }
            if (offsetTB != 0) {
                ViewCompat.offsetTopAndBottom(fab, offsetTB)
            }
            if (offsetLR != 0) {
                ViewCompat.offsetLeftAndRight(fab, offsetLR)
            }
        }
    }

    private fun getDescendantRect(parent: ViewGroup, descendant: View, out: Rect) {
        out[0, 0, descendant.width] = descendant.height
        ViewGroupUtilsHoneycomb.offsetDescendantRect(parent, descendant, out)
    }

    internal object ViewGroupUtilsHoneycomb {
        private val sMatrix = ThreadLocal<Matrix>()
        private val sRectF = ThreadLocal<RectF>()
        fun offsetDescendantRect(group: ViewGroup, child: View, rect: Rect) {
            var m = sMatrix.get()
            if (m == null) {
                m = Matrix()
                sMatrix.set(m)
            } else {
                m.reset()
            }
            offsetDescendantMatrix(group, child, m)
            var rectF = sRectF.get()
            if (rectF == null) {
                rectF = RectF()
                sRectF.set(rectF)
            }
            rectF.set(rect)
            m.mapRect(rectF)
            rect[(rectF.left + 0.5f).toInt(), (rectF.top + 0.5f).toInt(), (rectF.right + 0.5f).toInt()] =
                (rectF.bottom + 0.5f).toInt()
        }

        fun offsetDescendantMatrix(target: ViewParent, view: View, m: Matrix) {
            val parent = view.parent
            if (parent is View && parent !== target) {
                val vp = parent as View
                offsetDescendantMatrix(target, vp, m)
                m.preTranslate(-vp.scrollX.toFloat(), -vp.scrollY.toFloat())
            }
            m.preTranslate(view.left.toFloat(), view.top.toFloat())
            if (!view.matrix.isIdentity) {
                m.preConcat(view.matrix)
            }
        }
    }

}