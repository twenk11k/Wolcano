package com.wolcano.musicplayer.music.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.SparseArray
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class PermissionUtils private constructor(private val `object`: Any) {

    interface PermInterface {
        fun onPermGranted()
        fun onPermUnapproved()
    }

    private var permInterface: PermInterface? = null

    private lateinit var perms: Array<out String>

    fun permissions(vararg perms: String): PermissionUtils {
        this.perms = perms
        return this
    }

    fun result(result: PermInterface?): PermissionUtils {
        permInterface = result
        return this
    }

    fun requestPermissions() {
        val act = getAct(
            `object`
        ) ?: throw IllegalArgumentException("permission error")
        setPerms(act)
        for (permission in perms) {
            if (!permSets!!.contains(permission)) {
                if (permInterface != null) {
                    permInterface!!.onPermUnapproved()
                }
                return
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permInterface?.onPermGranted()
            return
        }
        val unapprovedPermsList = getUnapprovedPerms(act, perms)
        if (unapprovedPermsList.isEmpty()) {
            permInterface?.onPermGranted()
            return
        }
        val reqCode = genRequestCode()
        val unapprovedPerms = unapprovedPermsList.toTypedArray()
        requestPermissionss(`object`, unapprovedPerms, reqCode)
        sparseArray.put(reqCode, permInterface)
    }

    companion object {
        private var permSets: MutableSet<String>? = null
        private val atomicInteger = AtomicInteger(0)
        private val sparseArray = SparseArray<PermInterface?>()
        fun with(activity: Activity): PermissionUtils {
            return PermissionUtils(activity)
        }

        fun with(fragment: Fragment): PermissionUtils {
            return PermissionUtils(fragment)
        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            perms: Array<String?>,
            grantResults: IntArray
        ) {
            val result = sparseArray[requestCode]
                ?: return
            sparseArray.remove(requestCode)
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    result.onPermUnapproved()
                    return
                }
            }
            result.onPermGranted()
        }

        @TargetApi(Build.VERSION_CODES.M)
        private fun requestPermissionss(`object`: Any, permissions: Array<String>, requestCode: Int) {
            if (`object` is Activity) {
                `object`.requestPermissions(permissions, requestCode)
            } else if (`object` is Fragment) {
                `object`.requestPermissions(permissions, requestCode)
            }
        }

        private fun getUnapprovedPerms(context: Context, perms: Array<out String>): List<String> {
            val unapprovedPermsList: MutableList<String> = ArrayList()
            for (perm in perms) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        perm
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    unapprovedPermsList.add(perm)
                }
            }
            return unapprovedPermsList
        }

        @Synchronized
        private fun setPerms(context: Context) {
            if (permSets == null) {
                permSets = HashSet()
                try {
                    val packageInfo = context.packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.GET_PERMISSIONS
                    )
                    val permissions = packageInfo.requestedPermissions
                    Collections.addAll(permSets, *permissions)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

        private fun getAct(`object`: Any?): Activity? {
            if (`object` != null) {
                if (`object` is Activity) {
                    return `object`
                } else if (`object` is Fragment) {
                    return `object`.activity
                }
            }
            return null
        }

        private fun genRequestCode(): Int {
            return atomicInteger.incrementAndGet()
        }
    }

}