package com.luyin.permission60;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.luyin.permission60.permission.PermissionHandler;
import com.luyin.permission60.permission.PermissionHelper;
import com.luyin.permission60.permission.PermissionResultCallBack;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/10/26 09:35
 */
public class ChildOneFragment extends BaseFragment implements PermissionResultCallBack {
    private Button btnRequestPermission;

    private PermissionHandler permissionHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child1, container, false);
        btnRequestPermission = (Button) view.findViewById(R.id.btn_ChildRequestPermission);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        permissionHandler = PermissionHelper.getInstance().getPermissionHandler(this);
        permissionHandler.setPermissionResultCallBack(this);
        btnRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionHandler.requestPermission(111, Manifest.permission.CALL_PHONE);
            }
        });

    }

    @Override
    public void onRequestPermissionNewResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("ChildOneFragment", "requestCode: " + requestCode);
        if(requestCode==111){
//            Log.d("ChildOneFragment", "CALL_PHONE: " + grantResults[0]);
//            Log.d("ChildOneFragment", "CAMERA: " + grantResults[1]);
        }

    }

}
