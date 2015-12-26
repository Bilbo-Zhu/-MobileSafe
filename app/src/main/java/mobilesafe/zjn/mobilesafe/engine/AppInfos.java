package mobilesafe.zjn.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobilesafe.zjn.mobilesafe.bean.AppInfo;

/**
 * Created by zjn on 2015/12/19.
 */
public class AppInfos {
    public static List<AppInfo> getAppInfo(Context context){
        PackageManager packageManager = context.getPackageManager();//获取到包的管理者
        List<AppInfo> packageInfos = new ArrayList<>();

        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo installedPackage : installedPackages) {
            AppInfo appInfo = new AppInfo();

            Drawable drawable = installedPackage.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);

            String apkName = installedPackage.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);

            String packageName = installedPackage.packageName;
            appInfo.setApkPackageName(packageName);

            String sourceDir = installedPackage.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            long apkSize = file.length();
            appInfo.setApkSize(apkSize);

            int flags = installedPackage.applicationInfo.flags;
            if((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
               appInfo.setUserApp(false);
            }else {
                appInfo.setUserApp(true);
            }

            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                appInfo.setIsRom(false);
            }else{
                appInfo.setIsRom(true);
            }

            packageInfos.add(appInfo);

        }

        return packageInfos;
    }
}
