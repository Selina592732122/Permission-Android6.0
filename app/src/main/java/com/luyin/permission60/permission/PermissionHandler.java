package com.luyin.permission60.permission;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * * android 6.0的权限管理 做出了以下几个动作测试
 * （字段识别： R1 为第一次请求权限   R2为第二次请求权限  G（Grant） 通过 D(Deny) 拒绝  NA（not ask ）不再询问）
 * R1 G（程序永久得到权限） --->(代码再次请求)  action1: G(程序得到权限)  action2: D（程序取消权限）
 * R1 D --->R2 G(程序永久得到权限)  --->(代码再次请求) 回到R1  action1: G（得到权限） D（重启App 拒绝权限）
 * R1 D --->R2 D NA  --->(代码再次请求)无界面
 * 因此，
 * 1、只要用户允许了，程序就能得到权限了。无需重复申请
 * 2、如果用户NA且拒绝 ，那么需要弹出对话框引导用户去申请权限。
 * 注意：代码设计时候，一定要保证不能重复申请权限，否则使用shouldShowRequestPermissionRationale 会出现奇怪问题，程序设计失败。
 * <p>
 * 权限设计：友好的设计--->告知申请权限有什么作用 页面---->系统自带的对话框------> 永久拒绝（弹出引导对话框）
 *
 * @link https://github.com/k0shk0sh/PermissionHelper
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
     * @param activity    当前activity
     * @param permissions 权限列表
     */
    @Deprecated
    private void doPermissionWorkBeforeAndroidM(@NonNull Activity activity,
                                                @NonNull String[] permissions) {
//        for (String perm : permissions) {
//            if (permissionAction != null) {
//                if (ActivityCompat.checkSelfPermission(activity, perm)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    permissionAction.onResult(perm, PackageManager.PERMISSION_DENIED);
//                } else {
//                    permissionAction.onResult(perm, PackageManager.PERMISSION_GRANTED);
//                }
//            }
//        }
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

    private Context getContext(Target target) {
        Context context = null;
        if (target instanceof Activity) {
            context = (Context) target;
        } else if (target instanceof Fragment) {
            context = ((Fragment) target).getContext();
        }
        return context;
    }

    void filterPermission(Target target, @NonNull String[] permissions) {
        Context context = getContext(target);
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
    boolean
    isDeniedForever(String permissionName, int grantResult) {
        return grantResult == PermissionResultCallBack.PERMISSION_DENIED && !shouldShowRequestPermissionRationale(permissionName);

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

        if (permissionResultCallBack != null) {
            String[] permissionNameArray = new String[permissionList.size()];
            int[] grantResultArray = new int[permissionList.size()];

            for (int i = 0; i < permissionList.size(); i++) {
                permissionNameArray[i] = permissionList.get(i).getPermissionName();
                grantResultArray[i] = permissionList.get(i).getGrantResult();
            }
            permissionResultCallBack.onPermissionResult(this.requestCode, permissionNameArray, grantResultArray);
        }
        /**reset list*/
        permissionList.clear();
    }

    /**
     * Activity的权限请求 同步方法不能抽象，只能自行添加。(抽象方法 不能为同步或者静态方法，实现的方法可以为同步，但是不能静态)
     */
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
         * @param requestCode
         * @param permissions
         */
        @Override
        public void requestPermission(int requestCode, @NonNull String... permissions) {
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
            filterPermission(fragment, permissions);
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
