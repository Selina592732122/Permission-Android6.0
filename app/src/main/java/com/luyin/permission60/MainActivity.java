package com.luyin.permission60;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.luyin.permission60.permission.PermissionHandler;
import com.luyin.permission60.permission.PermissionHelper;
import com.luyin.permission60.permission.PermissionResultCallBack;

public class MainActivity extends BaseActivity implements PermissionResultCallBack {

    private PermissionHandler permissionHandler;
    private Fragment rootFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermissionHandler();
        setContentView(R.layout.activity_main);

        rootFragment = new RootFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fl_rootContainer, rootFragment).show(rootFragment).commit();
        findViewById(R.id.btnActivityRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionHandler.requestPermission(111, Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA);
            }
        });
    }

    private void initPermissionHandler() {
        permissionHandler = PermissionHelper.getInstance().getPermissionHandler(this);
        permissionHandler.setPermissionResultCallBack(this);
    }

    @Override
    public void onRequestPermissionNewResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("MainActivity", "requestCode: " + requestCode);
        if (requestCode == 111) {
//            Log.d("ProfileActivity", "CALL_PHONE: " + grantResults[0]);
//            Log.d("ProfileActivity", "CAMERA: " + grantResults[1]);
        }

    }
}
