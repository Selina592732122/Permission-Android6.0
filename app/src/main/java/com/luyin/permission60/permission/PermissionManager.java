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
public class PermissionManager {
    private static final String TAG = "PermissionManager";
    private static PermissionManager mInstance = null;
    private final Map<Object, PermissionHandler> handlerList = new HashMap<>();

    public static PermissionManager getInstance() {
        if (mInstance == null) {
            mInstance = new PermissionManager();
        }
        return mInstance;
    }

    private PermissionManager() {

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
     * @param context
     * @param permission
     * @return
     */
    public static synchronized boolean checkPermission(@Nullable Context context, String permission) {
        return checkPermissionList(context, new String[]{permission});
    }

    /**
     * 检查是否有权限
     *
     * @param context    上下文
     * @param permission 权限列表
     * @return
     */
    public static synchronized boolean checkPermissionList(@Nullable Context context,
                                                           String[] permission) {
        if (context == null) {
            return false;
        }

        boolean hasPermissions = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String perm : permission) {
                hasPermissions &= PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, perm);
            }

        } else {
            PackageManager packageManager = context.getPackageManager();
            for (String perm : permission) {
                hasPermissions &= PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(perm, context.getPackageName());
            }
        }

        return hasPermissions;
    }

    /**
     * 得到权限句柄
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
     * 得到权限句柄
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

    /**
     * 移除句柄
     * @param activity
     */
    public void unregisterPermissionHandler(Activity activity) {
        handlerList.remove(activity);
    }

    /**
     * 移除句柄
     * @param fragment
     */
    public void unregisterPermissionHandler(Fragment fragment) {
        handlerList.remove(fragment);
    }
//    /**
//     * Activity 权限操作
//     *
//     * @param activity
//     * @return
//     */
    //  @SuppressWarnings("unchecked")
//    private PermissionHandler createPermissionHandler(Activity activity) {
//        PermissionHandler permissionHandler=handlerList.get(activity);
//        if (permissionHandler == null) {
//            permissionHandler = PermissionHandler.build(activity);
//        } else {
//            permissionHandler.bind(activity);
//        }
//        return permissionHandler;
//    }


}
