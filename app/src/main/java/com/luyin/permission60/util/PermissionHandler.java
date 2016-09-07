package com.luyin.permission60.util;

import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/8/22 14:02
 * Email：rainyeveningstreet@gmail.com
 */
public abstract class PermissionHandler implements PermissionAction {
    private Looper mLooper = Looper.getMainLooper();

    public synchronized final void onResult(@NonNull String permission, int grantResult) {
        if(grantResult== PackageManager.PERMISSION_GRANTED){
            onGrated(permission);
        }else if(grantResult== PackageManager.PERMISSION_DENIED){
            onDenied(permission);
        }

    }
}
