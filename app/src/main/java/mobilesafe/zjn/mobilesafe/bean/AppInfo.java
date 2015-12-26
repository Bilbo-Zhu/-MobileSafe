package mobilesafe.zjn.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by zjn on 2015/12/19.
 */
public class AppInfo {
    private Drawable icon;
    private String apkName;
    private long apkSize;
    private Boolean userApp;
    private Boolean isRom;
    private String apkPackageName;

    private String apkpath;


    public String getApkpath() {
        return apkpath;
    }

    public void setApkpath(String apkpath) {
        this.apkpath = apkpath;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long size) {
        this.apkSize = size;
    }

    public Boolean getUserApp() {
        return userApp;
    }

    public void setUserApp(Boolean userApp) {
        this.userApp = userApp;
    }

    public Boolean getIsRom() {
        return isRom;
    }

    public void setIsRom(Boolean isRom) {
        this.isRom = isRom;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }
}
