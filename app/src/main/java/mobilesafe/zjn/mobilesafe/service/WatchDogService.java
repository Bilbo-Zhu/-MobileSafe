package mobilesafe.zjn.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.List;

import mobilesafe.zjn.mobilesafe.activity.EnterPwdActivity;
import mobilesafe.zjn.mobilesafe.db.AppLockDao;

/**
 * Created by zjn on 2015/12/25.
 */
public class WatchDogService extends Service {

    private ActivityManager activityManager;
    private AppLockDao dao;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    //临时停止保护的包名
    private String tempStopProtectPackageName;

    private class WatchDogReceiver extends BroadcastReceiver{



        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("com.itheima.mobileguard.stopprotect")){
                //获取到停止保护的对象

                tempStopProtectPackageName = intent.getStringExtra("packageName");
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                tempStopProtectPackageName = null;
                // 让狗休息
                falg = false;
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //让狗继续干活
                if(falg == false){
                    startWatDog();
                }
            }


        }

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();


        dao = new AppLockDao(this);

        appLockInfos = dao.findAll();

        //注册广播接受者


        receiver = new WatchDogReceiver();

        IntentFilter filter = new IntentFilter();
        //停止保护
        filter.addAction("com.itheima.mobileguard.stopprotect");

        //注册一个锁屏的广播
        /**
         * 当屏幕锁住的时候。狗就休息
         * 屏幕解锁的时候。让狗活过来
         */
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        filter.addAction(Intent.ACTION_SCREEN_ON);


        registerReceiver(receiver, filter);



        //获取到进程管理器

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);



        //1 首先需要获取到当前的任务栈

        //2取任务栈最上面的任务

        startWatDog();
    }
    //标记当前的看萌狗是否停下来
    private boolean falg = false;
    private List<String> appLockInfos;
    private WatchDogReceiver receiver;

    private void startWatDog() {

        new Thread(){
            public void run() {
                falg = true;
                while (falg) {
                    //由于这个狗一直在后台运行。为了避免程序阻塞。
                    //获取到当前正在运行的任务栈
                    List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
                    //获取到最上面的进程
                    ActivityManager.RunningTaskInfo taskInfo = tasks.get(0);
                    //获取到最顶端应用程序的包名
                    String packageName = taskInfo.topActivity.getPackageName();

                    System.out.println(packageName);
                    //让狗休息一会
                    SystemClock.sleep(30);
                    //直接从数据库里面查找当前的数据
                    //这个可以优化。改成从内存当中寻找
                    if(appLockInfos.contains(packageName)){
//					if(dao.find(packageName)){
//						System.out.println("在程序锁数据库里面");

                        //说明需要临时取消保护
                        //是因为用户输入了正确的密码
                        if(packageName.equals(tempStopProtectPackageName)){

                        }else{
                            Intent intent = new Intent(WatchDogService.this,EnterPwdActivity.class);
                            /**
                             * 需要注意：如果是在服务里面往activity界面跳的话。需要设置flag
                             */
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //停止保护的对象
                            intent.putExtra("packageName", packageName);

                            startActivity(intent);
                        }



                    }



                }


            };
        }.start();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        falg = false;
        unregisterReceiver(receiver);
        receiver = null;
    }

}

