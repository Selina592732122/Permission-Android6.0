package com.luyin.permission60;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.luyin.permission60.permission.PermissionHandler;
import com.luyin.permission60.permission.PermissionHelper;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/10/25 11:01
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 添加以下代码
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHandler permissionHandler = PermissionHelper.getInstance().getPermissionHandler(this);
        permissionHandler.notifyPermissionChange(requestCode, permissions, grantResults);
    }
}
