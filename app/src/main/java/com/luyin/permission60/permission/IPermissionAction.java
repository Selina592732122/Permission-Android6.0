package com.luyin.permission60.permission;

import android.content.pm.PackageManager;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/8/22 11:05
 * Email：rainyeveningstreet@gmail.com
 */
public interface IPermissionAction {
    /**
     * @see android.content.pm.PackageManager#PERMISSION_GRANTED
     */
    int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
    /**
     * @see android.content.pm.PackageManager#PERMISSION_DENIED
     */
    int PERMISSION_DENIED = PackageManager.PERMISSION_DENIED;

    /**
     * 权限通过
     *
     * @param permission
     */
    void onGrated(String permission);

    /**
     * 权限拒绝 （一般显示用户引导）
     *
     * @param permission
     */
    void onDenied(String permission);




}
