package com.luyin.permission60.permission;

import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/8/22 14:02
 * Email：rainyeveningstreet@gmail.com
 */
public abstract class PermissionAction implements IPermissionAction {
    private Looper mLooper = Looper.getMainLooper();

    public synchronized final void onResult(@NonNull String permission, int grantResult) {
        if (grantResult == PERMISSION_GRANTED) {
            onGrated(permission);
        } else if (grantResult == PERMISSION_DENIED) {
            onDenied(permission);
        }
    }
}
