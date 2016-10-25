package com.luyin.permission60.permission;

import android.content.pm.PackageManager;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/10/24 15:43
 */
public interface PermissionResultCallBack {
    int PERMISSION_UNDEFINED = -111;
    /**
     * @see PackageManager#PERMISSION_GRANTED
     */
    int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
    /**
     * @see PackageManager#PERMISSION_DENIED
     */
    int PERMISSION_DENIED = PackageManager.PERMISSION_DENIED;
    /**
     * 不再提示
     */
    int PERMISSION_DENIED_FOREVER = 111;

    void onPermissionResult(int requestCode, String[] permissions, int[] grantResults);
}
