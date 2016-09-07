package com.luyin.permission60.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/8/20 00:54
 * Email：rainyeveningstreet@gmail.com
 */
public class PermissionManager {
    private static final String TAG = "PermissionManager";
    private static PermissionManager mInstance = null;
    public static final int BASE_REQUEST_PERMISSION_CODE = 1;
    private PermissionHandler permissionHandler;

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
    public synchronized boolean hasPermission(@Nullable Context context,
                                              String permission) {
        return hasAssignPermissions(context, new String[]{permission});
    }

    /**
     * 检查是否有权限
     *
     * @param context    上下文
     * @param permission 权限列表
     * @return
     */
    public synchronized boolean hasAssignPermissions(@Nullable Context context,
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
    /***************************************************权限授予了，再次请求不再请求授予，直接执行*****************************************************************************************/
    /**
     * activity单权限申请
     *
     * @param activity          指定的activity
     * @param permission        权限
     * @param permissionHandler 权限动作
     */
    public synchronized void requestPermissionForResult(@NonNull Activity activity,
                                                        @NonNull String permission, PermissionHandler permissionHandler) {
        if (!hasPermission(activity, permission)) {
            requestPermissionsForResult(activity, permissionHandler, new String[]{permission});
        }
    }

    /**
     * activity多权限申请
     *
     * @param activity    指定的activity
     * @param permissions 权限列表
     */
    public synchronized void requestPermissionsForResult(@NonNull Activity activity,
                                                         PermissionHandler permissionHandler,
                                                         @NonNull String[] permissions) {
        this.permissionHandler = permissionHandler;
        if (permissions.length > 0) {
            List<String> noAssignPermission = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (!hasPermission(activity, permissions[i])) {
                    noAssignPermission.add(permissions[i]);
                } else {

                }
            }
            if (noAssignPermission.size() > 0) {
                ActivityCompat.requestPermissions(activity, noAssignPermission.toArray(new String[noAssignPermission.size()]), BASE_REQUEST_PERMISSION_CODE);
            }

        }


    }

    /**
     * fragment单权限申请
     *
     * @param fragment   当前fragment
     * @param permission 权限名称
     */
    public synchronized void requestPermissionForResult(@NonNull Fragment fragment,
                                                        @NonNull String permission,
                                                        PermissionHandler permissionHandler) {
        requestPermissionsForResult(fragment, permissionHandler, new String[]{permission});
    }

    /**
     * fragment多权限申请
     *
     * @param fragment    当前fragment
     * @param permissions 权限列表
     */
    public synchronized void requestPermissionsForResult(@NonNull Fragment fragment,
                                                         PermissionHandler permissionHandler,
                                                         @NonNull String[] permissions) {
        if (permissions.length > 0) {
            List<String> noAssignPermission = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                //TODO context可能会为空
                if (!hasPermission(fragment.getContext(), permissions[i])) {
                    noAssignPermission.add(permissions[i]);
                } else {
                    if (permissionHandler != null) {
                        permissionHandler.onResult(permissions[i], PackageManager.PERMISSION_GRANTED);
                    }

                }
            }
            if (noAssignPermission.size() > 0) {
                fragment.requestPermissions(noAssignPermission.toArray(new String[noAssignPermission.size()]), BASE_REQUEST_PERMISSION_CODE);
            }

        }

    }

    /**
     * 通知权限改变 请在onRequestPermissionsResult手动调用次方法
     *
     * @param requestCode  请求码 固定值BASE_REQUEST_PERMISSION_CODE
     * @param permissions  权限列表
     * @param grantResults 权限请求结果码
     */
    public synchronized void notifyPermissionChange(int requestCode,
                                                    @NonNull String[] permissions,
                                                    @NonNull int[] grantResults) {
        if (permissionHandler == null && permissions.length == 0 && requestCode != BASE_REQUEST_PERMISSION_CODE) {
            return;
        }

        if (permissionHandler != null) {
            for (int index = 0; index < permissions.length; index++) {
                permissionHandler.onResult(permissions[index], grantResults[index]);
            }
        }


    }

    /**
     * Android M之前的申请权限行为
     *
     * @param activity    当前activity
     * @param permissions 权限列表
     */
    @Deprecated
    private void doPermissionWorkBeforeAndroidM(@NonNull Activity activity,
                                                @NonNull String[] permissions) {
        for (String perm : permissions) {
            if (permissionHandler != null) {
                if (ActivityCompat.checkSelfPermission(activity, perm)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionHandler.onResult(perm, PackageManager.PERMISSION_DENIED);
                } else {
                    permissionHandler.onResult(perm, PackageManager.PERMISSION_GRANTED);
                }
            }
        }
    }

    // ActivityCompat.shouldShowRequestPermissionRationale(activity,permission);
    // fragment.shouldShowRequestPermissionRationale()
    //onRequestPermissionResult(requestCode,String[] permissions,int[] grantResults)
}
