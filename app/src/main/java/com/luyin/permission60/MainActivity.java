package com.luyin.permission60;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.luyin.permission60.util.PermissionHandler;
import com.luyin.permission60.util.PermissionManager;

public class MainActivity extends AppCompatActivity {
    private PermissionManager permissionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionManager = PermissionManager.getInstance();
        setContentView(R.layout.activity_main);
        permissionManager.requestPermissionsForResult(this, new PermissionHandler() {
            @Override
            public void onGrated(String permission) {
                if (Manifest.permission.CALL_PHONE.equals(permission)) {
                    Log.d("QQQ", "onGrated: execute Call Phone");

                } else if (Manifest.permission.CAMERA.equals(permission)) {
                    Log.d("QQQ", "onGrated: execute CAMERA");
                }
            }

            @Override
            public void onDenied(String permission) {
                if (Manifest.permission.CALL_PHONE.equals(permission)) {
                    Log.d("QQQ", "onDenied Call Phone");
                } else if (Manifest.permission.CAMERA.equals(permission)) {
                    Log.d("QQQ", "onDenied Call CAMERA");
                }


            }
        }, new String[]{Manifest.permission.CALL_PHONE
                , Manifest.permission.CAMERA});
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (permissionManager != null) {
            permissionManager.notifyPermissionChange(requestCode, permissions, grantResults);
        }
    }
}
