# Permission-Android6.0

目前在Android 6.0 模拟器上使用成功,支持Activity、Fragment、以及嵌套的Fragment权限请求<p/>
支持小米手机的请求逻辑（未在小米（6.0系统）手机进行测试）<p/>
##写在前面
1、之前将权限申请的结果回调分为三个方法onGrated(String permission)，onDenied(String permission)，onRationale(String[] permission).但是后面使用的时候发现，这样书写不利于比较复杂的权限申请。官方的onRequestPermissionsResult 这个方法参数很详细了，不需要再去过度封装。<p/>
2、官方权限申请需要得到补充，补充如下：<p/>
<p/>
a.如果用户同意了权限申请，那么下次就不需要再申请，直接返回申请成功请求结果. <p/>
b.如果用户拒绝且勾选了“不再提示（not ask again）”，返回权限被永久拒绝结果码，以便程序员再次处理。<p/>
c.Fragment嵌套Fragment，嵌套内部的Fragment权限请求。
3、在小米上，shouldShowRequestPermissionRationale 始终为false 但是并不影响这个库的使用。希望有人测试。。
##类的联系
你只能通过PermissionHelper得到操作句柄，PermissionHelper 是一个单例模型。<p/>
PermissionHandler和你绑定的类（Acticity,Fragment）是一一对应。

##使用方式：
我这里以申请android.permission.CAMERA和android.permission.CALL_PHONE为例：

###Activity：
1、在AndroidMainifest添加危险权限（android.permission.CAMERA和android.permission.CALL_PHONE）<p/>
2、得到权限控制句柄<p/>
```
 permissionHandler = PermissionManager.getInstance().getPermissionHandler(this);
```
3、调用：
```
 无请求码的
 permissionHandler.requestPermission(Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA);
 有请求码的
 permissionHandler.requestPermission(2，Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA);
 
```
4、在你的Activity去implements PermissionResultCallBack.<p/>
5、重写onRequestPermissionNewResult 实现你自己的请求代码逻辑<p/>
6、建议在你的BaseActivity里面添加如下代码：<p/>
```
  @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHandler permissionHandler = PermissionHelper.getInstance().getPermissionHandler(this);
        permissionHandler.notifyPermissionChange(requestCode, permissions, grantResults);
    }
```

###Fragment
操作和Activity一样，不过也是要建议在你的BaseFragment里面添加如下代码<p/>
```
  @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHandler permissionHandler = PermissionHelper.getInstance().getPermissionHandler(this);
        permissionHandler.notifyPermissionChange(requestCode, permissions, grantResults);
    }
```
注：官方已经把权限申请当作是Activity的一部分，而这个库是权限申请的一个补充，所以建议你这么写，一定记得标注final<p/>
##写在最后
1、在一个目标（Activity,Fragment）请求权限不要超过3个，很影响用户体验，当然你真的需要这么做，那你就要注意自己的逻辑了。<p/>
2、或许你会觉得这个库的过度封装。我觉得这个得看个人。我是比较崇尚可维护，可读性，使用难度小，职责分明这个角度来的。<p/>
3、反射实现的代码，使用起来在Java可读性可谓是最强的，但是对于手机，就算这几年性能得到的提升，也应该尽量避免使用反射。当然适当的反射我是比较推崇的，比如使用Retrofit和ButterKnife。当然了，网上有人使用编译时注解，其实无非就是把请求码放在比较显眼的地方，阅读起来整体提升不大<p/>
4、如果你使用这个权限方案出现问题，请及时Issue，我随时在线。<p/>

