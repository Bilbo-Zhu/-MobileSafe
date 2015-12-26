package mobilesafe.zjn.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by zjn on 2015/12/22.
 */
public class TaskInfo {

    private Drawable icon;

    private String packageName;

    private String appName;

    private long memorySize;


    /**
     * 是否是用户进程
     */
    private boolean userApp;

    /**
     * 判断当前的item的条目是否被勾选上
     */
    private boolean checked;


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    @Override
    public String toString() {
        return "TaskInfo [packageName=" + packageName + ", appName=" + appName
                + ", memorySize=" + memorySize + ", userApp=" + userApp + "]";
    }
}

