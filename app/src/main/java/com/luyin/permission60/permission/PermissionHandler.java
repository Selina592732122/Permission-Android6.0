package com.luyin.permission60.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/9/18 22:57
 * Email：rainyeveningstreet@gmail.com
 */
public abstract class PermissionHandler<Target> {
    //可能需要一个集合缓存
    // private List<PermissionAction> permissionActionList = new ArrayList<>();
    protected PermissionAction permissionAction;
    protected OnRationaleListener onRationaleListener;
    public static final int BASE_REQUEST_PERMISSION_CODE = 1;
    protected List<String> unAssignPermissionList = new ArrayList<>();
    protected List<String> permissionRationale = new ArrayList<>();

    /**
     * @param activity
     * @return
     * @see PermissionManager#getPermissionHandler(Activity)
     */
    static PermissionHandler build(Activity activity) {
        return new ActivityPermissionHandler(activity);
    }

    /**
     * @param fragment
     * @return
     * @see PermissionManager#getPermissionHandler(Fragment)
     */
    static PermissionHandler build(Fragment fragment) {
        return new FragmentPermissionHandler(fragment);
    }


    public void setOnRationaleListener(OnRationaleListener onRationaleListener) {
        this.onRationaleListener = onRationaleListener;
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
        for (String perm : permissions) {
            if (permissionAction != null) {
                if (ActivityCompat.checkSelfPermission(activity, perm)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionAction.onResult(perm, PackageManager.PERMISSION_DENIED);
                } else {
                    permissionAction.onResult(perm, PackageManager.PERMISSION_GRANTED);
                }
            }
        }
    }

    public abstract boolean bind(Target target);

    public abstract void unbind();

    /**
     * 单权限请求
     *
     * @param permission 权限名字
     * @param permissionAction  动作回调
     */
    public abstract void requestPermission(@NonNull String permission, PermissionAction permissionAction);

    /**
     * 多权限申请
     *
     * @param permissionAction 动作回调
     * @param permissions      权限列表
     */
    public abstract void requestPermission(PermissionAction permissionAction, @NonNull String[] permissions);


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

    protected void requestBeforeAction(Target target, PermissionAction permissionAction, @NonNull String[] permissions) {
        this.permissionAction = permissionAction;
        Context context = getContext(target);
        /**
         * fragment 里面getContext 有可能返回为null
         */

        if (context == null) {
            return;
        }

        for (String permission : permissions) {
            if (!PermissionManager.checkPermission(context, permission)) {
                unAssignPermissionList.add(permission);
            } else {
                permissionAction.onResult(permission, IPermissionAction.PERMISSION_GRANTED);
            }
        }

        if (onRationaleListener != null) {
            for (String permission : permissions) {
                permissionRationale.add(permission);
            }
        }

    }

    /**
     * 通知权限改变 请在onRequestPermissionsResult手动调用次方法
     *
     * @param requestCode  请求码 固定值BASE_REQUEST_PERMISSION_CODE
     * @param permissions  权限列表
     * @param grantResults 权限请求结果码
     */
    public synchronized void notifyPermissionChange(int requestCode,
                                                    @NonNull String[] permissions,
                                                    @NonNull int[] grantResults) {
        if (permissionAction == null && permissions.length == 0 && requestCode != BASE_REQUEST_PERMISSION_CODE) {
            return;
        }

        if (permissionAction != null) {
            for (int index = 0; index < permissions.length; index++) {
                permissionAction.onResult(permissions[index], grantResults[index]);
            }
        }

        /**
         * 筛选不再提醒的权限
         */
        if (onRationaleListener != null) {
            Iterator<String> it = permissionRationale.iterator();

            while (it.hasNext()) {
                String permission = it.next();
                if (shouldShowRequestPermissionRationale(permission)) {
                    it.remove();
                }
            }

            if (permissionRationale.size() == 0) {
                onRationaleListener.onRationale(new String[]{""});
            } else {
                onRationaleListener.onRationale(permissionRationale.toArray(new String[permissionRationale.size()]));
            }

        }
        /**reset list*/
        permissionRationale.clear();
        unAssignPermissionList.clear();


    }


    private static class ActivityPermissionHandler extends PermissionHandler<Activity> {
        private Activity activity;

        public ActivityPermissionHandler(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void requestPermission(@NonNull String permission, PermissionAction permissionAction) {
            String[] permissions = new String[]{permission};
            requestPermission(permissionAction, permissions);
        }


        @Override
        public synchronized void requestPermission(PermissionAction permissionAction, @NonNull String[] permissions) {
            requestBeforeAction(activity, permissionAction, permissions);
            if (unAssignPermissionList.size() == 0) {
                return;
            }
            ActivityCompat.requestPermissions(activity, unAssignPermissionList.toArray(new String[unAssignPermissionList.size()]), BASE_REQUEST_PERMISSION_CODE);
        }


        @Override
        public synchronized boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
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
        public void requestPermission(@NonNull String permission, PermissionAction permissionAction) {
            String[] permissions = {permission};
            requestPermission(permissionAction, permissions);
        }

        @Override
        public synchronized void requestPermission(PermissionAction permissionAction, @NonNull String[] permissions) {
            requestBeforeAction(fragment, permissionAction, permissions);
            if (unAssignPermissionList.size() == 0) {
                return;
            }
            fragment.requestPermissions(unAssignPermissionList.toArray(new String[unAssignPermissionList.size()]), BASE_REQUEST_PERMISSION_CODE);
        }


        @Override
        public synchronized boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
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

    public interface OnRationaleListener {
        /**
         * 不再提醒权限申请回调
         *
         * @param permission 不再提醒的权限集合  （用于弹出对话框引导用户去申请权限）
         */
        void onRationale(String[] permission);
    }
}
