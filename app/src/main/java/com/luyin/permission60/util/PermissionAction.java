package com.luyin.permission60.util;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/8/22 11:05
 * Email：rainyeveningstreet@gmail.com
 */
public interface PermissionAction {

    //权限通过
    void onGrated(String permission);
    //权限拒绝 （一般显示用户引导）
    void onDenied(String permission);

}
