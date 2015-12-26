package mobilesafe.zjn.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.db.AntivirusDao;
import mobilesafe.zjn.mobilesafe.utils.MD5Utils;

/**
 * Created by zjn on 2015/12/23.
 */
public class AntivirusActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivReturn;
    private ImageView iv_scanning;
    private RotateAnimation rotateAnimation;
    private ProgressBar pb;
    private Message message;

    // 扫描开始
    protected static final int BEGING = 1;
    // 扫描中
    protected static final int SCANING = 2;
    // 扫描结束
    protected static final int FINISH = 3;
    private TextView tv_init_virus;
    private LinearLayout ll_content;
    private ScrollView scrollView;
    private ArrayList<ScanInfo> scanInfos;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_antivirus);

        ivReturn = (ImageView)findViewById(R.id.topbarturn_antivirus);
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ivReturn.setOnClickListener(this);
        initData();
        setAnimation();

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BEGING:
                    tv_init_virus.setText("初始化八核引擎");
                    break;

                case SCANING:
                    tv_init_virus.setText("扫描病毒中...");
                    // 病毒扫描中;
                    TextView child = new TextView(AntivirusActivity.this);
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    // 如果为true表示有病毒
                    if (scanInfo.desc) {
                        child.setTextColor(Color.RED);

                        child.setText(scanInfo.appName + "有病毒");

                    } else {

                        child.setTextColor(Color.GRAY);
//					// 为false表示没有病毒
                        child.setText(scanInfo.appName + "扫描安全");


                    }

                    ll_content.addView(child, 0);
                    //自动滚动
                    scrollView.post(new Runnable() {

                        @Override
                        public void run() {
                            //一直往下面进行滚动
                            scrollView.fullScroll(scrollView.FOCUS_DOWN);

                        }
                    });


                    //System.out.println(scanInfo.appName + "扫描安全");
                    break;
                case FINISH:
                    tv_init_virus.setText("扫描结束");

                    // 当扫描结束的时候。停止动画
                    iv_scanning.clearAnimation();

                    showSelectDialog();

                    break;

            }
        }
    };

    private void showSelectDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AntivirusActivity.this);
        dialog.setTitle("扫描结果");
        if(count == 0){
            dialog.setMessage("扫描结束，手机安全");
            dialog.setCancelable(false);
            dialog.setPositiveButton("重新扫描", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    initData();

                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else{
            dialog.setMessage("扫描结束，存在病毒，是否清理");
            dialog.setCancelable(false);
            dialog.setPositiveButton("清理", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //initData();

                    clearTrojan();

                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    private void clearTrojan() {
        for(ScanInfo scanInfo : scanInfos ){
            String packageName = scanInfo.packageName;

            Intent uninstall_localIntent = new Intent("android.intent.action.DELETE",
                    Uri.parse("package:" + packageName));
            startActivity(uninstall_localIntent);
        }

    }

    private void initData() {

        scanInfos = new ArrayList<>();
        count = 0;

        new Thread(){
            @Override
            public void run() {
                super.run();

                //System.out.println("daozhe------->run()");

                message = Message.obtain();

                message.what = BEGING;

                PackageManager packageManager = getPackageManager();
                // 获取到所有安装的应用程序
                List<PackageInfo> installedPackages = packageManager
                        .getInstalledPackages(0);
                // 返回手机上面安装了多少个应用程序
                int size = installedPackages.size();

                System.out.println("size"+size);
                // 设置进度条的最大值
                pb.setMax(size);

                int progress = 0;

                for (PackageInfo packageInfo : installedPackages) {

                    ScanInfo scanInfo = new ScanInfo();

                    // 获取到当前手机上面的app的名字
                    String appName = packageInfo.applicationInfo.loadLabel(
                            packageManager).toString();

                    scanInfo.appName = appName;

                    String packageName = packageInfo.applicationInfo.packageName;

                    scanInfo.packageName = packageName;

                    // 首先需要获取到每个应用程序的目录

                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    // 获取到文件的md5
                    String md5 = MD5Utils.getFileMd5(sourceDir);
                    // 判断当前的文件是否是病毒数据库里面
                    String desc = AntivirusDao.checkFileVirus(md5);

                    /*System.out.println("-------------------------");

                    System.out.println(appName);

                    System.out.println(md5);*/


//					03-06 07:37:32.505: I/System.out(23660): 垃圾
//					03-06 07:37:32.505: I/System.out(23660): 51dc6ba54cbfbcff299eb72e79e03668

//					["md5":"51dc6ba54cbfbcff299eb72e79e03668","desc":"蝗虫病毒赶快卸载","desc":"蝗虫病毒赶快卸载","desc":"蝗虫病毒赶快卸载"]


//					B7DA3864FD19C0B2390C9719E812E649
                    // 如果当前的描述信息等于null说明没有病毒
                    if (desc == null) {
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;

                        scanInfos.add(scanInfo);

                        count++;

                    }
                    progress++;

                    SystemClock.sleep(100);

                    pb.setProgress(progress);

                    message = Message.obtain();

                    message.what = SCANING;

                    message.obj = scanInfo;

                    handler.sendMessage(message);

                }

                message = Message.obtain();

                message.what = FINISH;

                handler.sendMessage(message);

            }
        }.start();
    }

    static class ScanInfo {
        boolean desc;
        String appName;
        String packageName;
    }

    private void setAnimation() {
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(3000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        iv_scanning.startAnimation(rotateAnimation);
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(this,HomeActivity.class));
        finish();

        overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);
    }

    @Override
    protected void showNextPage() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topbarturn_antivirus:

                showPreviousPage();

                break;

            default:
                break;
        }
    }
}
