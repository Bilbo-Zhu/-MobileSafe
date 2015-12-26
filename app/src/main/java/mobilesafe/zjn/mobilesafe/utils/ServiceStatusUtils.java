package mobilesafe.zjn.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by zjn on 2015/12/14.
 */
public class ServiceStatusUtils {

    public static Boolean isServiceRunning(Context ctx, String serviceName){
        ActivityManager am = (ActivityManager) ctx.getSystemService(ctx.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();

            if(className.equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
