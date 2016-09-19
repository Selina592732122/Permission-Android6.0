# Permission-Android6.0

目前在Android 6.0  使用成功

##类的联系
你只能通过PermissionManager得到操作句柄，PermissionManager 是一个单例模型。<p/>
PermissionHandler和你绑定的类（Acticity,Fragment）是一一对应的,不可另外得到PermissionHandler。

##使用方式：
以Activity为例：<p/>
1、得到权限控制句柄
```
 permissionHandler = PermissionManager.getInstance().getPermissionHandler(this);
```
2、调用：
```
  permissionHandler.requestPermission(new PermissionAction() {
            @Override
            public void onGrated(String permission) {
            //权限拥有
                if (Manifest.permission.CALL_PHONE.equals(permission)) {


                } else if (Manifest.permission.CAMERA.equals(permission)) {
                    
                }
            }

            @Override
            public void onDenied(String permission) {
            //权限拒绝
                if (Manifest.permission.CALL_PHONE.equals(permission)) {
                   
                } else if (Manifest.permission.CAMERA.equals(permission)) {
                   


            }
        }, new String[]{Manifest.permission.CALL_PHONE
                , Manifest.permission.CAMERA});

         //权限拒绝不显示对话框的回调
        permissionHandler.setOnRationaleListener(new PermissionHandler.OnRationaleListener() {
            @Override
            public void onRationale(String[] permission) {

            }
        });
```

3、在Activity的onRequestPermissionsResult中手动通知权限
```
 permissionHandler.notifyPermissionChange(requestCode, permissions, grantResults);
```

##注意
在一个目标（Activity,Fragment）请求权限不要超过3个，很影响用户体验。
虽然权限成功和拒绝的回调方法已经被标上的synchronized ，但是建议减少耗时动作。我对锁这块了解不深，暂时没法做出更好地优化，如果你对有耗时的需求，请使用别的开源框架试试。

##写在最后
如果你使用这个权限方案出现问题，请及时Issue，我随时在线。

