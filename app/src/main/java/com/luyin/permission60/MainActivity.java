package com.luyin.permission60;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.luyin.permission60.permission.PermissionAction;
import com.luyin.permission60.permission.PermissionHandler;
import com.luyin.permission60.permission.PermissionManager;

public class MainActivity extends AppCompatActivity {

    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionHandler = PermissionManager.getInstance().getPermissionHandler(this);
        setContentView(R.layout.activity_main);
        permissionHandler.requestPermission(new PermissionAction() {
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
        permissionHandler.setOnRationaleListener(new PermissionHandler.OnRationaleListener() {
            @Override
            public void onRationale(String[] permission) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionHandler != null) {
            permissionHandler.notifyPermissionChange(requestCode, permissions, grantResults);
        }
    }
}
