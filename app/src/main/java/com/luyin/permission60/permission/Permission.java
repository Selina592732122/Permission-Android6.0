package com.luyin.permission60.permission;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/10/24 14:38
 */
public class Permission {
    private int grantResult;
    private String permissionName;

    public Permission(String permissionName, int grantResult) {
        this.grantResult = grantResult;
        this.permissionName = permissionName;
    }

    public int getGrantResult() {
        return grantResult;
    }

    public void setGrantResult(int grantResult) {
        this.grantResult = grantResult;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof String) {
            String oStr = (String) o;
            return oStr.equals(getPermissionName());
        }
        return super.equals(o);
    }

}
