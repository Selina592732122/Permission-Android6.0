package com.luyin.permission60.permission;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：洪培林
 * Created Time:2016/9/18 22:57
 * Email：rainyeveningstreet@gmail.com
 */
public abstract class PermissionHandler<Target> {
    public static final int COMMON_REQUEST_PERMISSION_CODE = 1;
    private List<ActivityPermission> permissionList = new ArrayList<>();
    private PermissionResultCallBack permissionResultCallBack;
    protected int requestCode;

    public void setPermissionResultCallBack(PermissionResultCallBack permissionResultCallBack) {
        this.permissionResultCallBack = permissionResultCallBack;
    }

    /**
     * @param activity
     * @return
     * @see PermissionHelper#getPermissionHandler(Activity)
     */
    static PermissionHandler build(Activity activity) {
        return new ActivityPermissionHandler(activity);
    }

    /**
     * @param fragment
     * @return
     * @see PermissionHelper#getPermissionHandler(Fragment)
     */
    static PermissionHandler build(Fragment fragment) {
        return new FragmentPermissionHandler(fragment);
    }

    /**
     * Android M之前的申请权限行为
     *
     * @param context
     * @param permissions 权限列表
     */
    void requestPermissionWorkBeforeAndroidM(int requestCode, @NonNull Context context, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (PermissionHelper.checkPermission(context, permission)) {
                permissionList.add(new ActivityPermission(permission, PermissionResultCallBack.PERMISSION_GRANTED));
            } else {
                permissionList.add(new ActivityPermission(permission, PermissionResultCallBack.PERMISSION_DENIED));
            }
        }
        this.requestCode = requestCode;
        notifyPermissionResultCallBack();

        permissionList.clear();
    }

    public abstract boolean bind(Target target);

    public abstract void unbind();

    /**
     * 公共权限请求
     *
     * @param permissions
     */
    public abstract void requestPermission(@NonNull String... permissions);

    public abstract void requestPermission(int requestCode, @NonNull String... permissions);

    /**
     * @param permission
     * @return 是否显示带 “Not ask again”选择框的权限申请对话框
     * @see Fragment#shouldShowRequestPermissionRationale(String)
     */
    public abstract boolean shouldShowRequestPermissionRationale(@NonNull String permission);


    void filterPermission(@Nullable Context context, @NonNull String[] permissions) {
        /**
         * fragment 里面getContext 有可能返回为null
         */
        if (context == null) {
            return;
        }
        for (String permission : permissions) {
            if (PermissionHelper.checkPermission(context, permission)) {
                permissionList.add(new ActivityPermission(permission, PermissionResultCallBack.PERMISSION_GRANTED));
            } else {
                permissionList.add(new ActivityPermission(permission, PermissionResultCallBack.PERMISSION_UNDEFINED));
            }
        }

    }

    String[] getRequestPermissionArray() {
        List<String> requestPermission = new ArrayList<>();
        for (ActivityPermission permission : permissionList) {
            if (permission.getGrantResult() == PermissionResultCallBack.PERMISSION_UNDEFINED) {
                requestPermission.add(permission.getPermissionName());

            }
        }
        if (requestPermission.size() == 0) {
            return null;
        }
        return requestPermission.toArray(new String[requestPermission.size()]);
    }

    /**
     * 是否永久拒绝
     *
     * @param permissionName
     * @param grantResult    notifyPermissionChange的grantResult
     * @return
     */
    boolean isDeniedForever(String permissionName, int grantResult) {
        return grantResult == PermissionResultCallBack.PERMISSION_DENIED && !shouldShowRequestPermissionRationale(permissionName);

    }

    /**
     * 通知回调
     */
    private void notifyPermissionResultCallBack() {
        if (permissionResultCallBack != null) {
            String[] permissionNameArray = new String[permissionList.size()];
            int[] grantResultArray = new int[permissionList.size()];

            for (int i = 0; i < permissionList.size(); i++) {
                permissionNameArray[i] = permissionList.get(i).getPermissionName();
                grantResultArray[i] = permissionList.get(i).getGrantResult();
            }
            permissionResultCallBack.onPermissionResult(requestCode, permissionNameArray, grantResultArray);
        }
    }

    /**
     * 通知权限改变 请在onRequestPermissionsResult手动调用次方法
     *
     * @param requestCode  请求码 固定值BASE_REQUEST_PERMISSION_CODE
     * @param permissions  权限列表
     * @param grantResults 权限请求结果码
     */
    public void
    notifyPermissionChange(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (permissions.length == 0 && requestCode != this.requestCode) {
            return;
        }

        for (int resultPermissionIndex = 0; resultPermissionIndex < permissions.length; resultPermissionIndex++) {
            for (ActivityPermission permission : permissionList) {
                if (permission.equals(permissions[resultPermissionIndex])) {
                    if (isDeniedForever(permissions[resultPermissionIndex], grantResults[resultPermissionIndex])) {
                        permission.setGrantResult(PermissionResultCallBack.PERMISSION_DENIED_FOREVER);
                    } else {
                        permission.setGrantResult(grantResults[resultPermissionIndex]);
                    }
                    break;
                }
            }
        }

        notifyPermissionResultCallBack();

        /**reset list*/
        permissionList.clear();
    }

    private static class ActivityPermissionHandler extends PermissionHandler<Activity> {
        private Activity activity;

        public ActivityPermissionHandler(Activity activity) {
            this.activity = activity;
        }

        /**
         * activity多权限申请
         *
         * @param permissions 权限列表
         */
        @Override
        public void requestPermission(@NonNull String... permissions) {
            requestCode = COMMON_REQUEST_PERMISSION_CODE;
            requestPermission(requestCode, permissions);
        }

        /**
         * 带请求码的权限申请
         *
         * @param requestCode
         * @param permissions
         */
        @Override
        public void requestPermission(int requestCode, @NonNull String... permissions) {
            if (Build.VERSION.SDK_INT < 23) {
                requestPermissionWorkBeforeAndroidM(requestCode, activity, permissions);
                return;
            }

            filterPermission(activity, permissions);
            String[] requestPermissionArray = getRequestPermissionArray();
            if (requestPermissionArray == null) {
                return;
            }

            this.requestCode = requestCode;
            ActivityCompat.requestPermissions(activity, requestPermissionArray, this.requestCode);
        }

        /**
         * @param permission
         * @return 是否显示带有never ask again 选择框
         * @see ActivityCompat#shouldShowRequestPermissionRationale(Activity, String)
         */
        @Override
        public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }

        @Override
        public boolean bind(Activity activity) {
            if (this.activity == activity) {
                return false;
            }
            unbind();
            this.activity = activity;
            return true;
        }


        @Override
        public void unbind() {
            this.activity = null;
        }
    }


    private static class FragmentPermissionHandler extends PermissionHandler<Fragment> {
        private Fragment fragment;

        public FragmentPermissionHandler(Fragment fragment) {
            this.fragment = fragment;
        }


        @Override
        public void requestPermission(@NonNull String... permissions) {
            requestCode = COMMON_REQUEST_PERMISSION_CODE;
            requestPermission(requestCode, permissions);

        }

        @Override
        public void requestPermission(int requestCode, @NonNull String... permissions) {

            if (Build.VERSION.SDK_INT < 23) {
                requestPermissionWorkBeforeAndroidM(requestCode, fragment.getContext(), permissions);
                return;
            }

            filterPermission(fragment.getContext(), permissions);
            String[] requestPermissionArray = getRequestPermissionArray();
            if (requestPermissionArray == null) {
                return;
            }
            this.requestCode = requestCode;
            fragment.requestPermissions(requestPermissionArray, this.requestCode);
        }


        @Override
        public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
            return fragment.shouldShowRequestPermissionRationale(permission);
        }

        @Override
        public boolean bind(Fragment fragment) {
            if (this.fragment == fragment) {
                return false;
            }
            unbind();
            this.fragment = fragment;
            return true;
        }

        @Override
        public void unbind() {
            fragment = null;

        }
    }


}
