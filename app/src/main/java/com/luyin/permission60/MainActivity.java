package com.luyin.permission60;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.luyin.permission60.permission.PermissionHandler;
import com.luyin.permission60.permission.PermissionHelper;
import com.luyin.permission60.permission.PermissionResultCallBack;

public class MainActivity extends BaseActivity implements PermissionResultCallBack {

    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermissionHandler();
        setContentView(R.layout.activity_main);

    }

    private void initPermissionHandler() {
        permissionHandler = PermissionHelper.getInstance().getPermissionHandler(this);
        permissionHandler.setPermissionResultCallBack(this);
    }

    public void requestPermission(View v) {
        permissionHandler.requestPermission(3,Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA);
    }

    @Override
    public void onPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("ProfileActivity", "requestCode: " + requestCode);
        Log.d("ProfileActivity", "CALL_PHONE: " + grantResults[0]);
        Log.d("ProfileActivity", "CAMERA: " + grantResults[1]);
    }
}
