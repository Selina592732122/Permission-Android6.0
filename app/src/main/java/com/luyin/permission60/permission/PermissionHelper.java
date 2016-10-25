package com.luyin.permission60.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/8/20 00:54
 * Email：rainyeveningstreet@gmail.com
 */
public class PermissionHelper {
    private static final String TAG = "PermissionManager";
    private static PermissionHelper mInstance = null;
    private final Map<Object, PermissionHandler> handlerList = new HashMap<>();

    public static PermissionHelper getInstance() {
        if (mInstance == null) {
            mInstance = new PermissionHelper();
        }
        return mInstance;
    }

    private PermissionHelper() {

    }

    /**
     * 获取权限列表
     *
     * @param activity
     * @return
     */
    public synchronized String[] getManifestPermissions(@NonNull final Activity activity) {
        PackageInfo packageInfo = null;
        List<String> list = new ArrayList<>(1);
        try {
            //Log.d(TAG, activity.getPackageName());
            packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            //Log.e(TAG, "A problem occurred when retrieving permissions", e);
        }
        if (packageInfo != null) {
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null) {
                for (String perm : permissions) {
                    //  Log.d(TAG, "Manifest contained permission: " + perm);
                    list.add(perm);
                }
            }
        }

        return list.toArray(new String[list.size()]);
    }



    /**
     * 检查是否有权限
     *
     * @param context    上下文
     * @param permissions 权限列表
     * @return
     */
    public static synchronized boolean checkPermission(@Nullable Context context,
                                                           String... permissions) {
        if (context == null) {
            return false;
        }

        boolean hasPermissions = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String perm : permissions) {
                hasPermissions &= PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, perm);
            }

        } else {
            PackageManager packageManager = context.getPackageManager();
            for (String perm : permissions) {
                hasPermissions &= PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(perm, context.getPackageName());
            }
        }

        return hasPermissions;
    }

    /**
     * 获得权限操作句柄  句柄和Activity 一一对应，不可更改
     *
     * @param activity
     * @return
     */
    public PermissionHandler getPermissionHandler(Activity activity) {
        PermissionHandler permissionHandler = handlerList.get(activity);
        if (permissionHandler == null) {
            permissionHandler = PermissionHandler.build(activity);
            handlerList.put(activity, permissionHandler);
        }
        return permissionHandler;
    }

    /**
     * 获得权限操作句柄
     *
     * @param fragment
     * @return
     */
    public PermissionHandler getPermissionHandler(Fragment fragment) {
        PermissionHandler permissionHandler = handlerList.get(fragment);

        if (permissionHandler == null) {
            permissionHandler = PermissionHandler.build(fragment);
            handlerList.put(fragment, permissionHandler);
        }
        return permissionHandler;
    }

    public void unregisterPermissionHandler(Activity activity) {
        handlerList.remove(activity);
    }

    public void unregisterPermissionHandler(Fragment fragment) {
        handlerList.remove(fragment);
    }

}
