package com.luyin.permission60;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.luyin.permission60.permission.PermissionHandler;
import com.luyin.permission60.permission.PermissionHelper;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/10/26 09:48
 */
public class BaseFragment extends Fragment{

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHandler permissionHandler = PermissionHelper.getInstance().getPermissionHandler(this);
        permissionHandler.notifyPermissionChange(requestCode, permissions, grantResults);
    }
}
