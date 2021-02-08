package com.wolcano.musicplayer.music.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class PermissionUtils {

    public interface PermInterface {
        void onPermGranted();

        void onPermUnapproved();
    }

    private static Set<String> permSets;
    private PermInterface permInterface;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private Object object;
    private static SparseArray<PermInterface> sparseArray = new SparseArray<>();

    private PermissionUtils(Object object) {
        this.object = object;
    }

    public static PermissionUtils with(@NonNull Activity activity) {
        return new PermissionUtils(activity);
    }

    public static PermissionUtils with(@NonNull Fragment fragment) {
        return new PermissionUtils(fragment);
    }

    private String[] perms;

    public PermissionUtils permissions(@NonNull String... perms) {
        this.perms = perms;
        return this;
    }

    public PermissionUtils result(@Nullable PermInterface sonuc) {
        this.permInterface = sonuc;
        return this;
    }

    public void reqPerm() {
        Activity act = getAct(object);
        if (act == null) {
            throw new IllegalArgumentException("permission error");
        }
        setPerms(act);
        for (String permission : perms) {
            if (!permSets.contains(permission)) {
                if (permInterface != null) {
                    permInterface.onPermUnapproved();
                }
                return;
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (permInterface != null) {
                permInterface.onPermGranted();
            }
            return;
        }

        List<String> unapprovedPermsList = getUnapprovedPerms(act, perms);
        if (unapprovedPermsList.isEmpty()) {
            if (permInterface != null) {
                permInterface.onPermGranted();
            }
            return;
        }
        int reqCode = genRequestCode();
        String[] unapprovedPerms = unapprovedPermsList.toArray(new String[unapprovedPermsList.size()]);
        reqPerms(object, unapprovedPerms, reqCode);
        sparseArray.put(reqCode, permInterface);
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] grantResults) {
        PermInterface sonuc = sparseArray.get(requestCode);

        if (sonuc == null) {
            return;
        }

        sparseArray.remove(requestCode);

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                sonuc.onPermUnapproved();
                return;
            }
        }
        sonuc.onPermGranted();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void reqPerms(Object object, String[] permissions, int requestCode) {
        if (object instanceof Activity) {
            ((Activity) object).requestPermissions(permissions, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(permissions, requestCode);
        }
    }

    private static List<String> getUnapprovedPerms(Context context, String[] perms) {
        List<String> unapprovedPermsList = new ArrayList<>();
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                unapprovedPermsList.add(perm);
            }
        }
        return unapprovedPermsList;
    }

    private static synchronized void setPerms(Context context) {
        if (permSets == null) {
            permSets = new HashSet<>();
            try {
                PackageInfo pckgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
                String[] perms = pckgInfo.requestedPermissions;
                Collections.addAll(permSets, perms);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static Activity getAct(Object object) {
        if (object != null) {
            if (object instanceof Activity) {
                return (Activity) object;
            } else if (object instanceof Fragment) {
                return ((Fragment) object).getActivity();
            }
        }
        return null;
    }

    private static int genRequestCode() {
        return atomicInteger.incrementAndGet();
    }
}